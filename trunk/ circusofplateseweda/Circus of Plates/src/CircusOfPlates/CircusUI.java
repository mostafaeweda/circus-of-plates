package CircusOfPlates;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;

/**
 * Graphical representation of the game manipulated by arrays data structures
 * such as queues, stacks and arrays Works as a view and controller of that
 * manipulates the model layer of abstraction The class is internally
 * implemented to oscillate between the windows as the state pattern to provide
 * one window handling to all user actions without the need to external
 * dialogues
 * 
 * @author Mostafa Mahmod Mahmod Eweda
 * @version 1.0
 * @see Plate
 * @see PlateBar
 * @see PlateSky
 * @since JDK 1.6
 */
public class CircusUI implements Observer {

	private static final int UPDATE_TIME = 20;
	private static int keyboardStep = 5;
	private static CircusUI instance;

	private Display display;
	private PlateBar[] bars;
	private ArrayList<Player> players;
	private Shell shell;
	private Canvas canvas;
	private Point last;
	private Runnable updateRunnable;
	private Rectangle canvasBounds;
	private Font font;
	private CLabel score1;
	private CLabel score2;
	private Composite currentComposite;
	private FormData formData;
	private TraverseListener transientTraverser;
	private boolean firstClown;
	private Image clown1Img;
	private Image clown2Img;
	private Image backgroundImg1;
	private Image backgroundImg2;
	private Color stickColor;
	private int barLevelNum = 2;
	private volatile boolean connected = false;
	private Server server;
	private Client client;
	private String hostIP = "127.0.0.1";

	public static synchronized CircusUI getInstance() {
		if (instance == null)
			return instance = new CircusUI();
		return instance;
	}

	private CircusUI() {
		firstClown = true;
		updateRunnable = new Runnable() {
			@Override
			public void run() {
				if (display.isDisposed() || shell.isDisposed()) {
					display.timerExec(-1, this);
					return;
				}
				for (int i = 0; i < bars.length; i++)
					bars[i].update();
				PlateSky.getInstance().update();
				if (!canvas.isDisposed()) {
					canvas.redraw();
					display.timerExec(UPDATE_TIME, this);
				}
			}
		};
		transientTraverser = new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent e) {
				if (e.keyCode == SWT.ESC) {
					players.clear();
					currentComposite.dispose();
					startUpComposite();
					shell.layout();
					shell.removeTraverseListener(this);
				}
			}
		};
	}

	private void run() {
		last = new Point(0, 0);
		players = new ArrayList<Player>(2);
		display = new Display();
		shell = new Shell(display, SWT.NO_TRIM);
		font = new Font(display, "Comic Sans MS", 18, SWT.BOLD);
		clown1Img = new Image(display, new ImageData(CircusUI.class
				.getResourceAsStream("clown1.png")).scaledTo(250, 250));
		clown2Img = new Image(display, new ImageData(CircusUI.class
				.getResourceAsStream("clown2.png")).scaledTo(250, 250));
		createContents();
		shell.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				dispose();
				System.exit(0);
			}
		});
		shell.setBounds(display.getBounds());
//		shell.setFullScreen(true);
//		shell.setMaximized(true);
		shell.open();
		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();
	}

	private void dispose() {
		PlateBar.dispose();
		font.dispose();
		display.dispose();
		clown1Img.dispose();
		clown2Img.dispose();
	}

	private void createContents() {
		shell.setLayout(new FillLayout());
		formData = new FormData();
		formData.left = new FormAttachment(0, 0);
		formData.top = new FormAttachment(0, 0);
		formData.right = new FormAttachment(80, 0);
		formData.bottom = new FormAttachment(100, 0);
		Rectangle dispBounds = display.getBounds();
		canvasBounds = new Rectangle(dispBounds.x, dispBounds.y,
				dispBounds.width - dispBounds.width * 20 / 100,
				dispBounds.height);
		startUpComposite();
	}

	private void startUpComposite() {
		PlateSky.getInstance().setRepeatTimes(0);
		PlateSky.getInstance().setAirShift(0);

		final Composite composite = new Composite(shell, SWT.NONE);
		Rectangle dispBounds = display.getBounds();
		backgroundImg2 = new Image(display, new ImageData(CircusUI.class
				.getResourceAsStream("space.png")).scaledTo(
				dispBounds.width, dispBounds.height));
		composite.setBackgroundImage(backgroundImg2);
		composite.setLayout(new FormLayout());
		FormData data = new FormData();
		data.left = new FormAttachment(25, 0);
		data.top = new FormAttachment(25, 0);
		data.right = new FormAttachment(75, 0);
		data.bottom = new FormAttachment(75, 0);
		Composite controls = new Composite(composite, SWT.NONE);
		controls.setLayoutData(data);
		controls.setLayout(new GridLayout(2, false));

		Button newGame = new Button(controls, SWT.NONE);
		newGame.setFont(font);
		GridData gridDat = new GridData(GridData.FILL_HORIZONTAL);
		gridDat.horizontalSpan = 2;
		newGame.setLayoutData(gridDat);
		newGame.setAlignment(SWT.CENTER);
		newGame.setText("New Game");
		shell.setDefaultButton(newGame);
		newGame.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (! connected) {
					newGame();
				}
				else if (client.isWorking()) {
					client.sendData("New Game");
					PlateBar.createFirst();
					newGame();
				}
			}
		});
		Button exit = new Button(controls, SWT.NONE);
		exit.setFont(font);
		gridDat.horizontalSpan = 2;
		exit.setLayoutData(gridDat);
		exit.setAlignment(SWT.CENTER);
		exit.setText("Exit");
		exit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}
		});

		ExpandBar prefrences = new ExpandBar(controls, SWT.V_SCROLL);
		prefrences.setForeground(display.getSystemColor(SWT.COLOR_BLUE));
		prefrences.setFont(font);
		prefrences.setLayoutData(new GridData(GridData.FILL_BOTH));
		prefrences.setBackground(display.getSystemColor(SWT.COLOR_BLACK));

		Composite settingsComposite = new Composite(prefrences, SWT.NONE);
		settingsComposite.setLayout(new GridLayout(2, false));
		CLabel plates = new CLabel(settingsComposite, SWT.NONE);
		plates.setFont(font);
		plates.setText("Plate Rate");
		plates.setToolTipText("frequency of plates generated");
		final Scale plateFrequency = new Scale(settingsComposite,
				SWT.HORIZONTAL);
		plateFrequency.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		plateFrequency.setMinimum(0);
		plateFrequency.setMaximum(10);
		plateFrequency.setSelection(5);
		plateFrequency.setIncrement(1);
		Plate.setPlateSpacing(Plate.PLATE_WIDTH + 5 * 10);
		plateFrequency.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Plate.setPlateSpacing(Plate.PLATE_WIDTH
						+ (plateFrequency.getSelection() * 10));
			}
		});

		CLabel speed = new CLabel(settingsComposite, SWT.NONE);
		speed.setFont(font);
		speed.setText("Game Speed");
		speed.setToolTipText("frequency of plates generated");
		final Scale speedFrequency = new Scale(settingsComposite,
				SWT.HORIZONTAL);
		speedFrequency.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		PlateBar.setSpeed(2);
		PlateSky.setSpeed(2);
		speedFrequency.setMinimum(1);
		speedFrequency.setMaximum(10);
		speedFrequency.setSelection(2);
		speedFrequency.setIncrement(1);
		speedFrequency.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int speed = speedFrequency.getSelection();
				PlateBar.setSpeed(speed);
				PlateSky.setSpeed(speed);
			}
		});

		CLabel air = new CLabel(settingsComposite, SWT.NONE);
		air.setFont(font);
		air.setText("Air Factor");
		final Scale airShift = new Scale(settingsComposite, SWT.HORIZONTAL);
		airShift.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		airShift.setMinimum(0);
		airShift.setMaximum(10);
		airShift.setIncrement(1);
		airShift.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				PlateSky.getInstance().setAirShift(airShift.getSelection());
			}
		});
		CLabel frequency = new CLabel(settingsComposite, SWT.NONE);
		frequency.setText("Air Frequency");
		frequency.setFont(font);
		final Scale airFrequency = new Scale(settingsComposite, SWT.HORIZONTAL);
		airFrequency.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		airFrequency.setMinimum(0);
		airFrequency.setMaximum(40);
		airFrequency.setIncrement(1);
		airFrequency.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				PlateSky.getInstance().setRepeatTimes(
						airFrequency.getSelection());
			}
		});
		final CLabel levels = new CLabel(settingsComposite, SWT.NONE);
		levels.setFont(font);
		levels.setAlignment(SWT.CENTER);
		levels.setText("Levels number");
		levels.setToolTipText("Levels number");
		final Scale levelsScale = new Scale(settingsComposite, SWT.HORIZONTAL);
		levelsScale.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		levelsScale.setMinimum(1);
		levelsScale.setMaximum(5);
		levelsScale.setIncrement(1);
		levelsScale.setSelection(2);
		levelsScale.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int selection = levelsScale.getSelection();
				levels.setText(selection + "");
				barLevelNum = selection;
			}
		});

		Composite controlsComposite = new Composite(prefrences, SWT.NONE);
		controlsComposite.setLayout(new GridLayout(2, false));
		final CLabel secondPlayerStep = new CLabel(controlsComposite, SWT.NONE);
		secondPlayerStep.setFont(font);
		secondPlayerStep.setAlignment(SWT.CENTER);
		secondPlayerStep.setText("2nd Player Step");
		secondPlayerStep.setToolTipText("Second Player Step");
		final Scale secondStep = new Scale(controlsComposite, SWT.HORIZONTAL);
		secondStep.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		secondStep.setMinimum(1);
		secondStep.setMaximum(50);
		secondStep.setIncrement(1);
		secondStep.setSelection(5);
		secondStep.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int selection = levelsScale.getSelection();
				keyboardStep += selection;
				secondPlayerStep.setText(keyboardStep + "");
			}
		});
		final CLabel connectLabel = new CLabel(controlsComposite, SWT.NONE);
		connectLabel.setFont(font);
		connectLabel.setAlignment(SWT.CENTER);
		connectLabel.setText("Network Game");
		final Button connect = new Button(controlsComposite, SWT.PUSH);
		connect.setText("Conect");
		connect.setFont(font);
		connect.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		connect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				connectLabel.setText("Connecting");
				connect.setEnabled(false);
				client = new Client(CircusUI.this, hostIP);
				server = new Server(CircusUI.this, display);
				Runnable r = new Runnable() {
					@Override
					public void run() {
						try {
							connected = true;
							client.runClient();
						} catch (Throwable e1) {
							e1.printStackTrace();
							System.out.println("iam server");
							server.runServer();
						}
					}
				};
				new Thread(r).start();
			}
		});

		ExpandItem expandItem = new ExpandItem(prefrences, SWT.NONE);
		expandItem.setText("Preferences");
		expandItem.setControl(settingsComposite);
		expandItem.setHeight(settingsComposite.computeSize(SWT.DEFAULT,
				SWT.DEFAULT).y);
		expandItem.setExpanded(true);
		expandItem = new ExpandItem(prefrences, SWT.NONE);
		expandItem.setText("Controls");
		expandItem.setControl(controlsComposite);
		expandItem.setHeight(controlsComposite.computeSize(SWT.DEFAULT,
				SWT.DEFAULT).y);
		expandItem.setExpanded(false);
		composite.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				backgroundImg2.dispose();
			}
		});
		currentComposite = composite;
	}

	private void customComposite() {
		final Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new FormLayout());
		canvas = new Canvas(composite, SWT.DOUBLE_BUFFERED);
		backgroundImg1 = new Image(display, new ImageData(CircusUI.class
				.getResourceAsStream("space.jpg")).scaledTo(
				canvasBounds.width, canvasBounds.height));
		Rectangle dispBounds = display.getBounds();
		backgroundImg2 = new Image(display, new ImageData(CircusUI.class
				.getResourceAsStream("space.png")).scaledTo(
				dispBounds.width - canvasBounds.width, canvasBounds.height / 3));
		stickColor = new Color(display, 219, 82, 16);
		Color white = display.getSystemColor(SWT.COLOR_WHITE);
		Color black = display.getSystemColor(SWT.COLOR_BLACK);
		PaletteData palette = new PaletteData(new RGB[] { white.getRGB(),
				black.getRGB() });
		ImageData sourceData = new ImageData(16, 16, 1, palette);
		sourceData.transparentPixel = 0;
		final Cursor cursor = new Cursor(display, sourceData, 0, 0);
		canvas.setCursor(cursor);

		canvas.setLayoutData(formData);
		canvas.setBackgroundImage(backgroundImg1);
		canvas.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		canvas.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				if (bars == null)
					return;
				PlateSky.getInstance().draw(e.gc);
				Iterator<Player> iter = players.iterator();
				while (iter.hasNext())
					iter.next().draw(e.gc);
				for (int i = 0; i < bars.length; i++)
					bars[i].draw(e.gc);
				if (client != null && client.isWorking())
					System.out.println("client hoppa");
			}
		});
		canvas.addMouseMoveListener(new MouseMoveListener() {
			@Override
			public void mouseMove(MouseEvent e) {
				players.get(0).setXLocation(e.x);
				last.x = e.x;
				last.y = e.y;
			}
		});
		canvas.addMouseListener(new MouseAdapter() {
			private boolean stopped = false;

			@Override
			public void mouseDown(MouseEvent e) {
				if (stopped) {
					stopped = false;
					display.timerExec(UPDATE_TIME, updateRunnable);
				} else {
					display.timerExec(-1, updateRunnable);
					stopped = true;
				}
			}
		});
		shell.addTraverseListener(transientTraverser);
		final KeyListener secondPlayerController = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.ARROW_LEFT) {
					try {
						players.get(1).move(-keyboardStep);
					} catch (NullPointerException e1) {
						System.err.println("second player not created yet");
					}
				} else if (e.keyCode == SWT.ARROW_RIGHT) {
					try {
						players.get(1).move(keyboardStep);
						canvas.redraw();
					} catch (NullPointerException e1) {
						System.err.println("second player not created yet");
					}
				}
			}
		};
		final Runnable changeImages = new Runnable() {
			@Override
			public void run() {
				Iterator<Player> iter = players.iterator();
				if (firstClown) {
					firstClown = false;
					while (iter.hasNext())
						iter.next().setImage(clown2Img);
				} else {
					firstClown = true;
					while (iter.hasNext())
						iter.next().setImage(clown1Img);
				}
				display.timerExec(300, this);
			}
		};
		display.timerExec(300, changeImages);
		canvas.addKeyListener(secondPlayerController);
		composite.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				cursor.dispose();
				backgroundImg1.dispose();
				backgroundImg2.dispose();
				stickColor.dispose();
				shell.removeKeyListener(secondPlayerController);
				display.timerExec(-1, updateRunnable);
				display.timerExec(-1, changeImages);

			}
		});
		currentComposite = composite;
		createControls(composite);
		if (connected) {
			if (server.isWorking())
				createBars();
		} else
			createBars();
		createPlayers();
	}

	private void createControls(Composite composite) {
		FormData data = new FormData();
		Color foreground = display.getSystemColor(SWT.COLOR_WHITE), background = display
				.getSystemColor(SWT.COLOR_BLACK);
		data.left = new FormAttachment(canvas, 0);
		data.top = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		data.bottom = new FormAttachment(100, 0);
		Composite controls = new Composite(composite, SWT.NONE);
		controls.setBackgroundImage(backgroundImg2);
		controls.setLayoutData(data);
		GridLayout gridLayout = new GridLayout(1, true);
		gridLayout.verticalSpacing = 0;
		controls.setLayout(gridLayout);
		Label space = new Label(controls, SWT.NONE);
		space.setLayoutData(new GridData(GridData.FILL_BOTH));
		space.setBackgroundImage(backgroundImg2);

		new Label(controls, SWT.SEPARATOR | SWT.HORIZONTAL)
				.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		score1 = new CLabel(controls, SWT.NONE);
		score1.setFont(font);
		score1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		score1.setText("Player: " + "Eweda" + "\nScore: 0");
		score1.setForeground(foreground);
		score1.setBackground(background);
		score1.setAlignment(SWT.CENTER);
		new Label(controls, SWT.SEPARATOR | SWT.HORIZONTAL)
				.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		score2 = new CLabel(controls, SWT.NONE);
		score2.setFont(font);
		score2.setForeground(foreground);
		score2.setBackground(background);
		score2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		score2.setText("Player: " + "Other" + "\nScore: 0");
		score2.setAlignment(SWT.CENTER);
		new Label(controls, SWT.SEPARATOR | SWT.HORIZONTAL)
				.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		/*
		 * CLabel server = new CLabel(controls, SWT.NONE);
		 * server.setForeground(foreground); server.setBackground(background);
		 * server.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		 * server.setFont(font); server.setAlignment(SWT.CENTER);
		 * server.setText("Connect"); server.addMouseListener(new MouseAdapter()
		 * {
		 * 
		 * @Override public void mouseDown(MouseEvent e) { Player player = new
		 * Player(CircusUI.this, "Eweda", new Image(display, new
		 * ImageData(CircusUI
		 * .class.getResourceAsStream("clown1.png")).scaledTo( 250, 250)),
		 * stickColor, new Point(0, display .getBounds().height - 40));
		 * players.add(player); canvas.setFocus(); } });
		 */
		new Label(controls, SWT.SEPARATOR | SWT.HORIZONTAL)
				.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		CLabel secondPlayer = new CLabel(controls, SWT.NONE);
		secondPlayer.setForeground(foreground);
		secondPlayer.setBackground(background);
		secondPlayer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		secondPlayer.setFont(font);
		secondPlayer.setAlignment(SWT.CENTER);
		secondPlayer.setText("Second Player");
		secondPlayer.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				Player player = new Player(CircusUI.this, "Mohammed",
						clown1Img, stickColor, new Point(0,
								display.getBounds().height - 40));
				shell.setData("Mohammed", score2);
				players.add(player);
				canvas.setFocus();
			}
		});

		new Label(controls, SWT.SEPARATOR | SWT.HORIZONTAL)
				.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		space = new Label(controls, SWT.NONE);
		space.setLayoutData(new GridData(GridData.FILL_BOTH));
		space.setBackgroundImage(backgroundImg2);
		data = new FormData();
		data.left = new FormAttachment(canvas, 0);
		data.top = new FormAttachment(0, 0);
		data.right = new FormAttachment(controls, 0);
		data.bottom = new FormAttachment(100, 0);
		new Label(composite, SWT.SEPARATOR).setLayoutData(data);
	}

	private void createBars() {
		bars = new PlateBar[2 * barLevelNum];
		bars[0] = new PlateBar(0, canvasBounds.width / 2 - 100, 50, 1, true);
		bars[1] = new PlateBar(canvasBounds.width,
				canvasBounds.width / 2 + 100, 50, 1, false);
		if (barLevelNum > 1) {
			bars[2] = new PlateBar(0, canvasBounds.width / 2 - 200, 100, 2,
					true);
			bars[3] = new PlateBar(canvasBounds.width,
					canvasBounds.width / 2 + 200, 100, 2, false);
		}
		if (barLevelNum > 2) {
			bars[4] = new PlateBar(0, canvasBounds.width / 2 - 300, 150, 3,
					true);
			bars[5] = new PlateBar(canvasBounds.width,
					canvasBounds.width / 2 + 300, 150, 3, false);
		}
		if (barLevelNum > 3) {
			bars[6] = new PlateBar(0, canvasBounds.width / 2 - 400, 200, 4,
					true);
			bars[7] = new PlateBar(canvasBounds.width,
					canvasBounds.width / 2 + 400, 200, 4, false);
		}
		if (barLevelNum > 4) {
			bars[8] = new PlateBar(0, canvasBounds.width / 2 - 500, 250, 5,
					true);
			bars[9] = new PlateBar(canvasBounds.width,
					canvasBounds.width / 2 + 500, 250, 5, false);
		}
		display.timerExec(UPDATE_TIME, updateRunnable);

		if (connected && server.isWorking()) {
			server.sendData(bars);
		}
	}

	private void createPlayers() {
		players.add(new Player(this, "Eweda", new Image(display, new ImageData(
				CircusUI.class.getResourceAsStream("clown1.png"))
				.scaledTo(250, 250)), stickColor, new Point(0, display
				.getBounds().height - 40)));
		shell.setData("Eweda", score1);
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	@Override
	public void update(Observable o, Object arg) {
		String msg = (String) arg;
		if ("score".equals(msg)) {
			String s = o.toString();
			CLabel score = (CLabel) shell.getData(s.substring(
					s.indexOf(' ') + 1, s.indexOf('\n')));
			score.setText(s);
		} else if (msg.startsWith("lost")) {
			currentComposite.dispose();
			endGameComposite();
			display.timerExec(-1, updateRunnable);
			shell.layout();
		}
	}

	private void endGameComposite() {
		final Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new FillLayout());
		final Font bigFont = new Font(display, "Comic Sans MS", 80, SWT.BOLD);
		final Font medFont = new Font(display, "Comic Sans MS", 50, SWT.BOLD);

		final Image image = new Image(display, 1000, 1000);
		Color blue = display.getSystemColor(SWT.COLOR_BLUE);
		Color yellow = display.getSystemColor(SWT.COLOR_YELLOW);
		Color white = display.getSystemColor(SWT.COLOR_WHITE);
		GC gc = new GC(image);
		gc.setBackground(white);
		gc.setForeground(yellow);
		gc.fillGradientRectangle(0, 0, 1000, 1000, true);
		for (int i = -500; i < 1000; i += 10) {
			gc.setForeground(blue);
			gc.drawLine(i, 0, 500 + i, 1000);
			gc.drawLine(500 + i, 0, i, 1000);
		}
		gc.dispose();

		final Pattern pattern;
		try {
			pattern = new Pattern(display, image);
		} catch (SWTException e) {
			// Advanced Graphics not supported.
			// This new API requires the Cairo Vector engine on GTK and Motif
			// and GDI+ on Windows.
			System.out.println(e.getMessage());
			return;
		}

		Canvas canvas = new Canvas(composite, SWT.DOUBLE_BUFFERED);
		canvas.addPaintListener(new PaintListener() {
			private Rectangle bounds = display.getBounds();

			@Override
			public void paintControl(PaintEvent e) {
				GC gc = e.gc;
				gc.setForegroundPattern(pattern);
				if (players.size() == 2)
					gc.setFont(medFont);
				else
					gc.setFont(bigFont);
				String msg = "Game Over\n" + players.get(0).toString();
				if (players.size() == 2)
					msg = "Game Over" + players.get(0).toString() + "\n"
							+ players.get(1).toString();
				Point pt = gc.stringExtent(msg);
				gc.drawString(msg, bounds.width / 2 - pt.x / 2, bounds.height
						/ 2 - pt.y / 2);
			}
		});
		composite.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				bigFont.dispose();
				pattern.dispose();
				medFont.dispose();
			}
		});
		shell.addTraverseListener(transientTraverser);
		currentComposite = composite;
	}

	public static void main(String[] args) {
		CircusUI.getInstance().run();
	}

	public void setPlateBars(PlateBar[] current) {
		bars = current;
		display.timerExec(UPDATE_TIME, updateRunnable);
	}

	public void newGame() {
		currentComposite.dispose();
		PlateSky.getInstance().clear();
		customComposite();
		shell.layout();
	}
}

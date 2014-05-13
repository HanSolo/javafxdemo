package eu.hansolo.fx;

import eu.hansolo.enzo.charts.SimpleRadarChart;
import eu.hansolo.enzo.clock.Clock;
import eu.hansolo.enzo.clock.ClockBuilder;
import eu.hansolo.enzo.common.Marker;
import eu.hansolo.enzo.common.Section;
import eu.hansolo.enzo.common.SymbolType;
import eu.hansolo.enzo.experimental.tbutton.TButton;
import eu.hansolo.enzo.experimental.tbutton.TButtonBuilder;
import eu.hansolo.enzo.flippanel.FlipPanel;
import eu.hansolo.enzo.fonts.Fonts;
import eu.hansolo.enzo.gauge.Gauge;
import eu.hansolo.enzo.gauge.GaugeBuilder;
import eu.hansolo.enzo.gauge.OneEightyGauge;
import eu.hansolo.enzo.gauge.OneEightyGaugeBuilder;
import eu.hansolo.enzo.gauge.SimpleGauge;
import eu.hansolo.enzo.gauge.SimpleGaugeBuilder;
import eu.hansolo.enzo.imgsplitflap.SplitFlap;
import eu.hansolo.enzo.imgsplitflap.SplitFlapBuilder;
import eu.hansolo.enzo.lcd.Lcd;
import eu.hansolo.enzo.lcd.LcdBuilder;
import eu.hansolo.enzo.lcd.LcdClock;
import eu.hansolo.enzo.lcd.LcdClockBuilder;
import eu.hansolo.enzo.led.Led;
import eu.hansolo.enzo.led.LedBuilder;
import eu.hansolo.enzo.ledbargraph.LedBargraph;
import eu.hansolo.enzo.ledbargraph.LedBargraphBuilder;
import eu.hansolo.enzo.matrixsegment.MatrixSegment;
import eu.hansolo.enzo.matrixsegment.MatrixSegmentBuilder;
import eu.hansolo.enzo.notification.Notification;
import eu.hansolo.enzo.notification.NotificationBuilder;
import eu.hansolo.enzo.notification.NotifierBuilder;
import eu.hansolo.enzo.onoffswitch.IconSwitch;
import eu.hansolo.enzo.onoffswitch.IconSwitchBuilder;
import eu.hansolo.enzo.onoffswitch.OnOffSwitch;
import eu.hansolo.enzo.onoffswitch.OnOffSwitchBuilder;
import eu.hansolo.enzo.qlocktwo.QlockTwo;
import eu.hansolo.enzo.qlocktwo.QlockTwoBuilder;
import eu.hansolo.enzo.radialmenu.RadialMenu;
import eu.hansolo.enzo.radialmenu.RadialMenuBuilder;
import eu.hansolo.enzo.radialmenu.RadialMenuItemBuilder;
import eu.hansolo.enzo.radialmenu.RadialMenuOptionsBuilder;
import eu.hansolo.enzo.roundlcdclock.RoundLcdClock;
import eu.hansolo.enzo.roundlcdclock.RoundLcdClockBuilder;
import eu.hansolo.enzo.sevensegment.SevenSegment;
import eu.hansolo.enzo.sevensegment.SevenSegmentBuilder;
import eu.hansolo.enzo.signaltower.SignalTower;
import eu.hansolo.enzo.signaltower.SignalTowerBuilder;
import eu.hansolo.enzo.simpleindicator.SimpleIndicator;
import eu.hansolo.enzo.simpleindicator.SimpleIndicatorBuilder;
import eu.hansolo.enzo.sixteensegment.SixteenSegment;
import eu.hansolo.enzo.sixteensegment.SixteenSegmentBuilder;
import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.PerspectiveTransform;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Random;


/**
 * User: hansolo
 * Date: 12.05.14
 * Time: 09:47
 */
public class Main extends Application {
    private static final Random         RND             = new Random();
    private static final Notification[] NOTIFICATIONS   = {
        NotificationBuilder.create().title("Info").message("New Information").image(Notification.INFO_ICON).build(),
        NotificationBuilder.create().title("Warning").message("Attention, somethings wrong").image(Notification.WARNING_ICON).build(),
        NotificationBuilder.create().title("Success").message("Great it works").image(Notification.SUCCESS_ICON).build(),
        NotificationBuilder.create().title("Error").message("ZOMG").image(Notification.ERROR_ICON).build()
    };
    private static final Dimension2D    SIZE            = new Dimension2D(500, 700);
    private static final double         INSET           = 0.05 * SIZE.getHeight();
    private static final double         PREF_PANE_WIDTH = SIZE.getWidth() * 0.9;
    private static final double         SHADOW_OPACITY  = 0.5;

    private static boolean mousePressed = false;
    private static double  touchX       = 0;
    private static double  lastTouchX   = 0;
    private static double  deltaX       = 0;
    private static double  touchStartX  = 0;
    private static double  touchStopX   = 0;
    private PixelReader          pixelReader;
    private WritableImage        image;
    private AnchorPane           contentPane;
    private Pane                 shadowOverlay;
    private AnchorPane           prefPane;
    private PerspectiveTransform transformLeft;
    private PerspectiveTransform transformRight;
    private ImageView            leftImage;
    private ImageView            rightImage;
    private Rectangle            shadowRect;
    private Timeline             timeline;
    private long                 pressedStart;

    private AnimationTimer timer;

    // ContentPane related
    private Label  header;
    private Button settingsButton;
    private Button exitButton;

    // PrefPane related
    private FlipPanel flipPanel1;
    private FlipPanel flipPanel2;

    // FlipPanel1 frontside    
    private ToggleGroup toggleGroup;
    private RadioButton radioButtonGauge;
    private RadioButton radioButtonSimpleGauge;
    private RadioButton radioButtonOneEightyGauge;
    private RadioButton radioButtonSimpleRadarChart;
    private RadioButton radioButtonLedBargraph;

    // FlipPanel1 backside
    private RadioButton radioButtonLed;
    private RadioButton radioButtonClock;
    private RadioButton radioButtonSplitFlap;
    private RadioButton radioButtonLcdClock;
    private RadioButton radioButtonSegments;


    // FlipPanel2 frontside
    private RadioButton radioButtonLcd;
    private RadioButton radioButtonSimpleIndicator;
    private RadioButton radioButtonRoundLcdClock;
    private RadioButton radioButtonNotification;
    private RadioButton radioButtonRadialMenu;

    // FlipPanel2 backside
    private RadioButton radioButtonSignalTower;
    private RadioButton radioButtonOnOffSwitch;
    private RadioButton radioButtonQlockTwo;
    private RadioButton radioButtonPushButton;


    // Gauge
    private StackPane gaugePane;
    private Gauge     gauge;
    private long      lastGaugeTimerCall;


    // SimpleGauge
    private StackPane   simpleGaugePane;
    private SimpleGauge simpleGauge;


    // OneEightyGauge
    private StackPane      oneEightyGaugePane;
    private OneEightyGauge oneEightyGauge;


    // RadarChart
    private StackPane        simpleRadarChartPane;
    private SimpleRadarChart simpleRadarChart;


    // LedBarGraph 
    private StackPane   ledBargraphPane;
    private LedBargraph ledBargraph1;
    private LedBargraph ledBargraph2;
    private LedBargraph ledBargraph3;
    private LedBargraph ledBargraph4;
    private LedBargraph ledBargraph5;
    private LedBargraph ledBargraph6;
    private LedBargraph ledBargraph7;
    private LedBargraph ledBargraph8;
    private LedBargraph ledBargraph9;
    private LedBargraph ledBargraph10;
    private Color[] ledBargraphColors = {
        Color.rgb(0, 180, 0),
        Color.rgb(0, 180, 0),
        Color.rgb(0, 180, 0),
        Color.rgb(0, 180, 0),
        Color.LIME,
        Color.LIME,
        Color.LIME,
        Color.LIME,
        Color.LIME,
        Color.LIME,
        Color.YELLOW,
        Color.YELLOW,
        Color.YELLOW,
        Color.RED,
        Color.RED
    };
    private long lastLedBargraphTimerCall;

    // Led
    private StackPane ledPane;
    private Led       led;


    // Clock
    private StackPane clockPane;
    private Clock     clock;


    // SplitFlap
    private StackPane splitFlapPane;
    private SplitFlap hourLeft;
    private SplitFlap hourRight;
    private SplitFlap minLeft;
    private SplitFlap minRight;
    private SplitFlap secLeft;
    private SplitFlap secRight;
    private long      lastClockTimerCall;
    private int       hours;
    private int       minutes;
    private int       seconds;

    // LcdClock
    private StackPane lcdClockPane;
    private LcdClock  lcdClock;

    // Segments    
    private StackPane      segmentsPane;
    private SevenSegment   sevenSeg1;
    private SevenSegment   sevenSeg2;
    private SevenSegment   sevenSeg3;
    private SevenSegment   sevenSeg4;
    private SevenSegment   sevenSeg5;
    private SixteenSegment sixteenSeg1;
    private SixteenSegment sixteenSeg2;
    private SixteenSegment sixteenSeg3;
    private SixteenSegment sixteenSeg4;
    private SixteenSegment sixteenSeg5;
    private MatrixSegment  matrixSeg1;
    private MatrixSegment  matrixSeg2;
    private MatrixSegment  matrixSeg3;
    private MatrixSegment  matrixSeg4;
    private MatrixSegment  matrixSeg5;
    private boolean        toggle;
    private long           lastSegmentTimerCall;

    // Lcd
    private StackPane lcdPane;
    private Lcd       lcd;

    // SimpleIndicator
    private StackPane       simpleIndicatorPane;
    private SimpleIndicator simpleIndicator1;
    private SimpleIndicator simpleIndicator2;
    private SimpleIndicator simpleIndicator3;
    private long            lastSimpleIndicatorCall;

    // RoundLcdClock
    private StackPane     roundLcdClockPane;
    private RoundLcdClock roundLcdClock;

    // Notification
    private StackPane             notificationPane;
    private Notification.Notifier notifier;

    // RadialMenu
    private StackPane  radialMenuPane;
    private RadialMenu radialMenu;

    // SignalTower
    private StackPane   signalTowerPane;
    private SignalTower signalTower;

    // OnOff Switch
    private StackPane   onOffPane;
    private OnOffSwitch onOffSwitch;
    private IconSwitch  iconSwitch;

    // QlockTwo
    private StackPane qlockTwoPane;
    private QlockTwo  qlockTwo;

    // TButton 
    private StackPane tButtonPane;
    private TButton   tButton1;


    @Override public void init() {
        timeline = new Timeline();

        timer = new AnimationTimer() {
            @Override public void handle(long now) {
                if (now > lastClockTimerCall + 500_000_000l) {
                    hours = LocalDateTime.now().getHour();
                    String hourString = Integer.toString(hours);
                    if (hours < 10) {
                        hourLeft.setText("0");
                        hourRight.setText(hourString.substring(0, 1));
                    } else {
                        hourLeft.setText(hourString.substring(0, 1));
                        hourRight.setText(hourString.substring(1));
                    }

                    minutes = LocalDateTime.now().getMinute();
                    String minutesString = Integer.toString(minutes);
                    if (minutes < 10) {
                        minLeft.setText("0");
                        minRight.setText(minutesString.substring(0, 1));
                    } else {
                        minLeft.setText(minutesString.substring(0, 1));
                        minRight.setText(minutesString.substring(1));
                    }

                    seconds = LocalDateTime.now().getSecond();
                    String secondsString = Integer.toString(seconds);
                    if (seconds < 10) {
                        secLeft.setText("0");
                        secRight.setText(secondsString.substring(0, 1));
                    } else {
                        secLeft.setText(secondsString.substring(0, 1));
                        secRight.setText(secondsString.substring(1));
                    }
                    lastClockTimerCall = now;
                }
                if (now > lastGaugeTimerCall + 5_000_000_000l) {
                    gauge.setValue(RND.nextDouble() * 100);
                    simpleGauge.setValue(RND.nextDouble() * 100);
                    oneEightyGauge.setValue(RND.nextDouble() * 100);
                    lcd.setValue(RND.nextDouble() * 100);
                    lastGaugeTimerCall = now;
                }
                if (now > lastSegmentTimerCall + 10_000_000_000l && !prefPane.isManaged()) {
                    toggle ^= true;
                    if (toggle) {
                        sevenSeg1.setCharacter(Integer.toString(RND.nextInt(9)));
                        sevenSeg2.setCharacter(Integer.toString(RND.nextInt(9)));
                        sevenSeg3.setCharacter(Integer.toString(RND.nextInt(9)));
                        sevenSeg4.setCharacter(Integer.toString(RND.nextInt(9)));
                        sevenSeg5.setCharacter(Integer.toString(RND.nextInt(9)));
                        matrixSeg1.setCharacter("R");
                        matrixSeg2.setCharacter("O");
                        matrixSeg3.setCharacter("C");
                        matrixSeg4.setCharacter("K");
                        matrixSeg5.setCharacter("S");
                        sixteenSeg1.setCharacter("J");
                        sixteenSeg2.setCharacter("a");
                        sixteenSeg3.setCharacter("v");
                        sixteenSeg4.setCharacter("a");
                        sixteenSeg5.setCharacter(" ");
                    } else {
                        sevenSeg1.setCharacter(Integer.toString(RND.nextInt(9)));
                        sevenSeg2.setCharacter(Integer.toString(RND.nextInt(9)));
                        sevenSeg3.setCharacter(Integer.toString(RND.nextInt(9)));
                        sevenSeg4.setCharacter(Integer.toString(RND.nextInt(9)));
                        sevenSeg5.setCharacter(Integer.toString(RND.nextInt(9)));
                        matrixSeg1.setCharacter("J");
                        matrixSeg2.setCharacter("a");
                        matrixSeg3.setCharacter("v");
                        matrixSeg4.setCharacter("a");
                        matrixSeg5.setCharacter(" ");
                        sixteenSeg1.setCharacter("R");
                        sixteenSeg2.setCharacter("O");
                        sixteenSeg3.setCharacter("C");
                        sixteenSeg4.setCharacter("K");
                        sixteenSeg5.setCharacter("S");
                    }
                    lastSegmentTimerCall = now;
                }
                if (now > lastLedBargraphTimerCall + 120_000_000l) {
                    ledBargraph1.setValue(RND.nextDouble());
                    ledBargraph2.setValue(RND.nextDouble());
                    ledBargraph3.setValue(RND.nextDouble());
                    ledBargraph4.setValue(RND.nextDouble());
                    ledBargraph5.setValue(RND.nextDouble());
                    ledBargraph6.setValue(RND.nextDouble());
                    ledBargraph7.setValue(RND.nextDouble());
                    ledBargraph8.setValue(RND.nextDouble());
                    ledBargraph9.setValue(RND.nextDouble());
                    ledBargraph10.setValue(RND.nextDouble());
                    lastLedBargraphTimerCall = now;
                }
                if (now > lastSimpleIndicatorCall + 5_000_000_000l) {
                    signalTower.setColors(false, false, false);
                    simpleIndicator1.setOn(false);
                    simpleIndicator2.setOn(false);
                    simpleIndicator3.setOn(false);
                    switch(RND.nextInt(3)) {
                        case 0:
                            simpleIndicator1.setOn(true);
                            signalTower.setGreenOn(true);
                            break;
                        case 1:
                            simpleIndicator2.setOn(true);
                            signalTower.setYellowOn(true);
                            break;
                        case 2:
                            simpleIndicator3.setOn(true);
                            signalTower.setRedOn(true);
                            break;
                    }
                    lastSimpleIndicatorCall = now;
                }                
            }
        };
        
        // FlipPanel1 frontside
        initGaugePane();
        initSimpleGaugePane();
        initOneEightyGaugePane();
        initSimpleRadarChartPane();
        initLedBargraphPane();

        // FlipPanel1 backside
        initLedPane();
        initClockPane();
        initSplitFlapPane();
        initLcdClockPane();
        initSegmentsPane();

        // FlipPanel2 frontside
        initLcdPane();
        initSimpleIndicatorPane();
        initRoundLcdClockPane();
        initNotificationPane();
        initRadialMenuPane();
        
        // FlipPanel2 backside
        initSignalTowerPane();
        initOnOffPane();
        initQlockTwoPane();
        initTButtonPane();
        
        initContentPane();
        initPrefPane();
        initShadowOverlay();
    }

    // FlipPanel1 Frontside
    private void initGaugePane() {
        gauge = GaugeBuilder.create()
                            .prefSize(500, 500)
                            .startAngle(330)
                            .angleRange(300)
                            .minValue(0)
                            .maxValue(100)
                            .sectionsVisible(true)
                            .sections(new Section(0, 60),
                                      new Section(60, 80),
                                      new Section(80, 100))
                            .areas(new Section(75, 100))
                            .markersVisible(true)
                            .markers(new Marker(40))
                            .majorTickSpace(20)
                            .plainValue(false)
                            .tickLabelOrientation(Gauge.TickLabelOrientation.HORIZONTAL)
                            .threshold(70)
                            .thresholdVisible(true)
                            .minMeasuredValueVisible(true)
                            .maxMeasuredValueVisible(true)
                            .title("Title")
                            .unit("Unit")
                            .build();
        gaugePane = new StackPane(gauge);

        AnchorPane.setLeftAnchor(gaugePane, 20d);
        AnchorPane.setTopAnchor(gaugePane, 50d);
        AnchorPane.setRightAnchor(gaugePane, 20d);

        //unManageNode(gaugePane);
    }
    private void initSimpleGaugePane() {
        simpleGauge = SimpleGaugeBuilder.create()
                                        .prefSize(500, 500)
                                        .sections(new Section(0, 10, "A++"),
                                                  new Section(10, 20, "A+"),
                                                  new Section(20, 30, "A"),
                                                  new Section(30, 40, "B"),
                                                  new Section(40, 50, "C"),
                                                  new Section(50, 60, "D"),
                                                  new Section(60, 70, "E"),
                                                  new Section(70, 80, "F"),
                                                  new Section(80, 90, "G"),
                                                  new Section(90, 100, "H"))
                                        .sectionTextVisible(true)
                                        .unit("W")
                                        .styleClass(SimpleGauge.STYLE_CLASS_GREEN_TO_RED_10)
                                        .build();
        simpleGaugePane = new StackPane(simpleGauge);

        AnchorPane.setLeftAnchor(simpleGaugePane, 20d);
        AnchorPane.setTopAnchor(simpleGaugePane, 50d);
        AnchorPane.setRightAnchor(simpleGaugePane, 20d);

        unManageNode(simpleGaugePane);
    }
    private void initOneEightyGaugePane() {
        oneEightyGauge = OneEightyGaugeBuilder.create()
                                              .prefSize(500, 500)
                                              .title("Temperature")
                                              .unit("°C")
                                              .maxValue(100)                                                    
                                              .barColor(Color.web("#f1c428"))                                              
                                              .build();
        oneEightyGaugePane = new StackPane(oneEightyGauge);

        AnchorPane.setLeftAnchor(oneEightyGaugePane, 20d);
        AnchorPane.setTopAnchor(oneEightyGaugePane, 50d);
        AnchorPane.setRightAnchor(oneEightyGaugePane, 20d);

        unManageNode(oneEightyGaugePane);
    }
    private void initSimpleRadarChartPane() {
        simpleRadarChart = new SimpleRadarChart();
        simpleRadarChart.setTitle("Temperature\n" + LocalDate.now());
        simpleRadarChart.setUnit("°C");
        simpleRadarChart.setScaleVisible(true);
        simpleRadarChart.setMinValue(-15);
        simpleRadarChart.setMaxValue(40);
        simpleRadarChart.setZeroLineVisible(true);
        simpleRadarChart.setFilled(true);
        simpleRadarChart.setNoOfSectors(24);
        simpleRadarChart.setPrefSize(500, 500);
        simpleRadarChart.setChartBackground(Color.web("#45617d"));
        simpleRadarChart.setScaleVisible(false);
        simpleRadarChart.setChartText(Color.WHITE);
        for (int i = 0; i <= 24; i++) {
            simpleRadarChart.addData(i, new XYChart.Data<>(i < 10 ? "0" + i + ":00" : i + ":00", RND.nextDouble() * 55 - 15));
        }
        simpleRadarChart.setGradientStops(new Stop(0.00000, Color.web("#3552a0")),
                               new Stop(0.09090, Color.web("#456acf")),
                               new Stop(0.27272, Color.web("#45a1cf")),
                               new Stop(0.36363, Color.web("#30c8c9")),
                               new Stop(0.45454, Color.web("#30c9af")),
                               new Stop(0.50909, Color.web("#56d483")),
                               new Stop(0.72727, Color.web("#9adb49")),
                               new Stop(0.81818, Color.web("#efd750")),
                               new Stop(0.90909, Color.web("#ef9850")),
                               new Stop(1.00000, Color.web("#ef6050")));
        simpleRadarChart.setPolygonMode(true);
        simpleRadarChartPane = new StackPane(simpleRadarChart);

        AnchorPane.setLeftAnchor(simpleRadarChartPane, 20d);
        AnchorPane.setTopAnchor(simpleRadarChartPane, 50d);
        AnchorPane.setRightAnchor(simpleRadarChartPane, 20d);
        
        unManageNode(simpleRadarChartPane);
    }
    private void initLedBargraphPane() {
        
        ledBargraph1 = LedBargraphBuilder.create().orientation(Orientation.VERTICAL).peakValueVisible(true).noOfLeds(15).build();
        ledBargraph2 = LedBargraphBuilder.create().orientation(Orientation.VERTICAL).peakValueVisible(true).noOfLeds(15).build();
        ledBargraph3 = LedBargraphBuilder.create().orientation(Orientation.VERTICAL).peakValueVisible(true).noOfLeds(15).build();
        ledBargraph4 = LedBargraphBuilder.create().orientation(Orientation.VERTICAL).peakValueVisible(true).noOfLeds(15).build();
        ledBargraph5 = LedBargraphBuilder.create().orientation(Orientation.VERTICAL).peakValueVisible(true).noOfLeds(15).build();
        ledBargraph6 = LedBargraphBuilder.create().orientation(Orientation.VERTICAL).peakValueVisible(true).noOfLeds(15).build();
        ledBargraph7 = LedBargraphBuilder.create().orientation(Orientation.VERTICAL).peakValueVisible(true).noOfLeds(15).build();
        ledBargraph8 = LedBargraphBuilder.create().orientation(Orientation.VERTICAL).peakValueVisible(true).noOfLeds(15).build();
        ledBargraph9 = LedBargraphBuilder.create().orientation(Orientation.VERTICAL).peakValueVisible(true).noOfLeds(15).build();
        ledBargraph10 = LedBargraphBuilder.create().orientation(Orientation.VERTICAL).peakValueVisible(true).noOfLeds(15).build();
        
        ledBargraph1.setLedColors(Arrays.asList(ledBargraphColors));
        ledBargraph2.setLedColors(Arrays.asList(ledBargraphColors));
        ledBargraph3.setLedColors(Arrays.asList(ledBargraphColors));
        ledBargraph4.setLedColors(Arrays.asList(ledBargraphColors));
        ledBargraph5.setLedColors(Arrays.asList(ledBargraphColors));
        ledBargraph6.setLedColors(Arrays.asList(ledBargraphColors));
        ledBargraph7.setLedColors(Arrays.asList(ledBargraphColors));
        ledBargraph8.setLedColors(Arrays.asList(ledBargraphColors));
        ledBargraph9.setLedColors(Arrays.asList(ledBargraphColors));
        ledBargraph10.setLedColors(Arrays.asList(ledBargraphColors));
        
        
        HBox ledBargraphBox = new HBox();
        ledBargraphBox.setPadding(new Insets(0, 50, 0, 0));
        ledBargraphBox.setSpacing(10);
        ledBargraphBox.getChildren().addAll(ledBargraph1, ledBargraph2,ledBargraph3, ledBargraph4, ledBargraph5,
                                            ledBargraph6, ledBargraph7, ledBargraph8, ledBargraph9, ledBargraph10);
        
        ledBargraphPane = new StackPane(ledBargraphBox);

        AnchorPane.setLeftAnchor(ledBargraphPane, 20d);
        AnchorPane.setTopAnchor(ledBargraphPane, 50d);
        AnchorPane.setRightAnchor(ledBargraphPane, 20d);
        
        unManageNode(ledBargraphPane);
    }
    
    // FlipPanel1 Backside
    private void initLedPane() {
        led     = LedBuilder.create()
                            .prefSize(400, 400)
                            .blinking(true)
                            .build();
        ledPane = new StackPane(led);

        AnchorPane.setLeftAnchor(ledPane, 20d);
        AnchorPane.setTopAnchor(ledPane, 50d);
        AnchorPane.setRightAnchor(ledPane, 20d);
        
        unManageNode(ledPane);
    }
    private void initClockPane() {
        clock     = ClockBuilder.create()
                                .prefSize(500, 500)
                                .design(Clock.Design.BRAUN)
                                .running(true)
                                .discreteSecond(true)
                                .build();
        clockPane = new StackPane(clock);

        AnchorPane.setLeftAnchor(clockPane, 20d);
        AnchorPane.setTopAnchor(clockPane, 50d);
        AnchorPane.setRightAnchor(clockPane, 20d);
        
        unManageNode(clockPane);
    }
    private void initSplitFlapPane() {
        hourLeft  = SplitFlapBuilder.create().prefWidth(80).prefHeight(137).flipTime(300).selection(SplitFlap.TIME_0_TO_5).textColor(Color.WHITESMOKE).build();
        hourRight = SplitFlapBuilder.create().prefWidth(80).prefHeight(137).flipTime(300).selection(SplitFlap.TIME_0_TO_9).textColor(Color.WHITESMOKE).build();
        minLeft   = SplitFlapBuilder.create().prefWidth(80).prefHeight(137).flipTime(300).selection(SplitFlap.TIME_0_TO_5).textColor(Color.WHITESMOKE).build();
        minRight  = SplitFlapBuilder.create().prefWidth(80).prefHeight(137).flipTime(300).selection(SplitFlap.TIME_0_TO_9).textColor(Color.WHITESMOKE).build();
        secLeft   = SplitFlapBuilder.create().prefWidth(80).prefHeight(137).flipTime(300).selection(SplitFlap.TIME_0_TO_5).textColor(Color.WHITESMOKE).build();
        secRight  = SplitFlapBuilder.create().prefWidth(80).prefHeight(137).flipTime(300).selection(SplitFlap.TIME_0_TO_9).textColor(Color.WHITESMOKE).build();
        HBox clockBox = new HBox();
        clockBox.setSpacing(0);
        //HBox.setMargin(hourRight, new Insets(0, 40, 0, 0));
        //HBox.setMargin(minRight, new Insets(0, 40, 0, 0));
        clockBox.getChildren().addAll(hourLeft, hourRight, minLeft, minRight, secLeft, secRight);
        
        splitFlapPane = new StackPane(clockBox);

        AnchorPane.setLeftAnchor(splitFlapPane, 20d);
        AnchorPane.setTopAnchor(splitFlapPane, 50d);
        AnchorPane.setRightAnchor(splitFlapPane, 20d);
        
        unManageNode(splitFlapPane);
    }
    private void initLcdClockPane() {
        lcdClock     = LcdClockBuilder.create()
                                      .prefSize(500, 500)                                      
                                      .styleClass(LcdClock.STYLE_CLASS_GREEN_DARKGREEN)
                                      .title("JavaFX")
                                      .build();
        lcdClockPane = new StackPane(lcdClock);

        AnchorPane.setLeftAnchor(lcdClockPane, 20d);
        AnchorPane.setTopAnchor(lcdClockPane, 50d);
        AnchorPane.setRightAnchor(lcdClockPane, 20d);
        
        unManageNode(lcdClockPane);
    }
    private void initSegmentsPane() {
        sevenSeg1   = SevenSegmentBuilder.create().segmentStyle(SevenSegment.SegmentStyle.CYAN).prefSize(71, 100).build();
        sevenSeg2   = SevenSegmentBuilder.create().segmentStyle(SevenSegment.SegmentStyle.CYAN).prefSize(71, 100).build();
        sevenSeg3   = SevenSegmentBuilder.create().segmentStyle(SevenSegment.SegmentStyle.CYAN).prefSize(71, 100).build();
        sevenSeg4   = SevenSegmentBuilder.create().segmentStyle(SevenSegment.SegmentStyle.CYAN).prefSize(71, 100).build();
        sevenSeg5   = SevenSegmentBuilder.create().segmentStyle(SevenSegment.SegmentStyle.CYAN).prefSize(71, 100).build();
        HBox sevenSegmentPane = new HBox();
        sevenSegmentPane.setSpacing(5);
        sevenSegmentPane.getChildren().setAll(sevenSeg1, sevenSeg2, sevenSeg3, sevenSeg4, sevenSeg5);        
        
        matrixSeg1  = MatrixSegmentBuilder.create().color(Color.LIME).prefSize(71, 100).character("J").build();
        matrixSeg2  = MatrixSegmentBuilder.create().color(Color.LIME).prefSize(71, 100).character("a").build();
        matrixSeg3  = MatrixSegmentBuilder.create().color(Color.LIME).prefSize(71, 100).character("v").build();
        matrixSeg4  = MatrixSegmentBuilder.create().color(Color.LIME).prefSize(71, 100).character("a").build();
        matrixSeg5  = MatrixSegmentBuilder.create().color(Color.LIME).prefSize(71, 100).character(" ").build();        
        HBox matrixSegmentPane = new HBox();
        matrixSegmentPane.setSpacing(5);        
        matrixSegmentPane.getChildren().setAll(matrixSeg1, matrixSeg2, matrixSeg3, matrixSeg4, matrixSeg5);

        sixteenSeg1 = SixteenSegmentBuilder.create().segmentStyle(SixteenSegment.SegmentStyle.WHITE).prefSize(71, 100).character("R").build();
        sixteenSeg2 = SixteenSegmentBuilder.create().segmentStyle(SixteenSegment.SegmentStyle.WHITE).prefSize(71, 100).character("O").build();
        sixteenSeg3 = SixteenSegmentBuilder.create().segmentStyle(SixteenSegment.SegmentStyle.WHITE).prefSize(71, 100).character("C").build();
        sixteenSeg4 = SixteenSegmentBuilder.create().segmentStyle(SixteenSegment.SegmentStyle.WHITE).prefSize(71, 100).character("K").build();
        sixteenSeg5 = SixteenSegmentBuilder.create().segmentStyle(SixteenSegment.SegmentStyle.WHITE).prefSize(71, 100).character("S").build();        
        HBox sixteenSegmentPane = new HBox();
        sixteenSegmentPane.setSpacing(5);
        sixteenSegmentPane.getChildren().setAll(sixteenSeg1, sixteenSeg2, sixteenSeg3, sixteenSeg4, sixteenSeg5);
        
        VBox segmentBox = new VBox(sevenSegmentPane, matrixSegmentPane, sixteenSegmentPane);
        segmentBox.setSpacing(30);
        segmentsPane    = new StackPane(segmentBox);

        AnchorPane.setLeftAnchor(segmentsPane, 20d);
        AnchorPane.setTopAnchor(segmentsPane, 50d);
        AnchorPane.setRightAnchor(segmentsPane, 20d);
        
        unManageNode(segmentsPane);
    }
    
    // FlipPanel2 Frontside
    private void initLcdPane() {
        lcd = LcdBuilder.create()
                        .prefWidth(500)
                        .prefHeight(156)
                        .styleClass(Lcd.STYLE_CLASS_AMBER)
                        .foregroundShadowVisible(true)
                        .crystalOverlayVisible(true)
                        .title("JavaFX")
                        .batteryVisible(true)
                        .signalVisible(true)
                        .alarmVisible(true)
                        .unit("°C")
                        .unitVisible(true)
                        .decimals(3)
                        .animationDurationInMs(1500)
                        .minMeasuredValueDecimals(2)
                        .minMeasuredValueVisible(true)
                        .maxMeasuredValueDecimals(2)
                        .maxMeasuredValueVisible(true)
                        .formerValueVisible(true)
                        .threshold(26)
                        .thresholdVisible(true)
                        .trendVisible(true)
                        .numberSystemVisible(false)
                        .lowerRightTextVisible(true)
                        .lowerRightText("rocks")
                        .valueFont(Lcd.LcdFont.LCD)
                        .animated(true)
                        .build();
        lcdPane = new StackPane(lcd);

        AnchorPane.setLeftAnchor(lcdPane, 20d);
        AnchorPane.setTopAnchor(lcdPane, 150d);
        AnchorPane.setRightAnchor(lcdPane, 20d);

        unManageNode(lcdPane);
    }
    private void initSimpleIndicatorPane() {
        simpleIndicator1 = SimpleIndicatorBuilder.create().indicatorStyle(SimpleIndicator.IndicatorStyle.RED).build();
        simpleIndicator2 = SimpleIndicatorBuilder.create().indicatorStyle(SimpleIndicator.IndicatorStyle.YELLOW).build();
        simpleIndicator3 = SimpleIndicatorBuilder.create().indicatorStyle(SimpleIndicator.IndicatorStyle.GREEN).build();
        
        simpleIndicator1.setMinSize(150, 150);
        simpleIndicator1.setPrefSize(150, 150);

        simpleIndicator2.setMinSize(150, 150);
        simpleIndicator2.setPrefSize(150, 150);

        simpleIndicator3.setMinSize(150, 150);
        simpleIndicator3.setPrefSize(150, 150);
        
        VBox simpleIndicatorBox = new VBox(simpleIndicator1, simpleIndicator2, simpleIndicator3);
        simpleIndicatorBox.setSpacing(20);
        
        simpleIndicatorPane = new StackPane(simpleIndicatorBox);

        AnchorPane.setLeftAnchor(simpleIndicatorPane, 20d);
        AnchorPane.setTopAnchor(simpleIndicatorPane, 100d);
        AnchorPane.setRightAnchor(simpleIndicatorPane, 20d);
        
        unManageNode(simpleIndicatorPane);
    }
    private void initRoundLcdClockPane() {
        roundLcdClock = RoundLcdClockBuilder.create()
                                            .color(Color.rgb(255, 255, 255, 1))
                                            .hourColor(Color.rgb(255, 255, 255, 0.75))
                                            .minuteColor(Color.rgb(255, 255, 255, 0.5))
                                            .secondColor(Color.WHITE)
                                            .timeColor(Color.WHITE)
                                            .prefSize(500, 500)
                                            .alarmVisible(true)
                                            .alarmOn(true)
                                            .alarm(LocalTime.now().plusSeconds(20))
                                            .dateVisible(true)
                                            .build();
        
        roundLcdClockPane = new StackPane(roundLcdClock);

        AnchorPane.setLeftAnchor(roundLcdClockPane, 20d);
        AnchorPane.setTopAnchor(roundLcdClockPane, 150d);
        AnchorPane.setRightAnchor(roundLcdClockPane, 20d);
        
        unManageNode(roundLcdClockPane);
    }
    private void initNotificationPane() {        
        Button button = new Button("Notify");
        button.setPrefSize(150, 50);
        button.setOnAction(event -> notifier.notify(NOTIFICATIONS[RND.nextInt(4)]));
        
        notificationPane = new StackPane(button);

        AnchorPane.setLeftAnchor(notificationPane, 150d);
        AnchorPane.setTopAnchor(notificationPane, 150d);

        unManageNode(notificationPane);        
    }
    private void initRadialMenuPane() {
        radialMenu = RadialMenuBuilder.create()
                                      .options(RadialMenuOptionsBuilder.create()
                                                                       .degrees(360)
                                                                       .offset(-90)
                                                                       .radius(200)
                                                                       .buttonSize(72)
                                                                       .buttonHideOnSelect(false)
                                                                       .buttonHideOnClose(false)
                                                                       .buttonAlpha(1.0)
                                                                       .buttonVisible(true)
                                                                       .build())
                                      .items(
                                          RadialMenuItemBuilder.create().symbol(SymbolType.SETTINGS).tooltip("Settings").size(48).build(),
                                          RadialMenuItemBuilder.create().symbol(SymbolType.LOCATION).tooltip("Location").size(48).build(),
                                          RadialMenuItemBuilder.create().selectable(true).symbol(SymbolType.MUSIC).tooltip("Music").size(48).build(),
                                          RadialMenuItemBuilder.create().symbol(SymbolType.HUMIDITY).tooltip("Humidity").size(48).build(),
                                          RadialMenuItemBuilder.create().symbol(SymbolType.TEMPERATURE1).tooltip("Temperature").size(48).build(),
                                          RadialMenuItemBuilder.create().symbol(SymbolType.BULB).tooltip("Ideas").size(48).build(),
                                          RadialMenuItemBuilder.create().symbol(SymbolType.HEAD_PHONES).tooltip("Sound").size(48).build(),
                                          RadialMenuItemBuilder.create().symbol(SymbolType.TWITTER).tooltip("Twitter").size(48).build(),
                                          RadialMenuItemBuilder.create().symbol(SymbolType.TAGS).tooltip("Tags").size(48).build(),
                                          RadialMenuItemBuilder.create().symbol(SymbolType.CART).tooltip("Shop").size(48).build(),
                                          RadialMenuItemBuilder.create().symbol(SymbolType.MULTI_RELAY).tooltip("MultiRelay").size(48).build(),
                                          RadialMenuItemBuilder.create().symbol(SymbolType.RELAY).tooltip("Relay").size(48).build())
                                      .build();
        radialMenu.setPrefSize(250, 250);
        
        radialMenuPane = new StackPane(radialMenu);

        AnchorPane.setLeftAnchor(radialMenuPane, 120d);
        AnchorPane.setTopAnchor(radialMenuPane, 250d);

        unManageNode(radialMenuPane);
    }
    
    // FlipPanel2 Backside
    private void initSignalTowerPane() {
        signalTower = SignalTowerBuilder.create().build();
        signalTowerPane = new StackPane(signalTower);

        AnchorPane.setLeftAnchor(signalTowerPane, 20d);
        AnchorPane.setTopAnchor(signalTowerPane, 50d);
        AnchorPane.setRightAnchor(signalTowerPane, 20d);

        unManageNode(signalTowerPane);
    }
    private void initOnOffPane() {
        onOffSwitch = OnOffSwitchBuilder.create().prefSize(150, 80).build();
        iconSwitch  = IconSwitchBuilder.create().prefSize(160, 64).symbolColor(Color.web("#34495e")).symbolType(SymbolType.BULB).build();
        
        VBox onOffBox = new VBox(onOffSwitch, iconSwitch);
        onOffBox.setSpacing(20);
        
        onOffPane = new StackPane(onOffBox);
        
        AnchorPane.setLeftAnchor(onOffPane, 20d);
        AnchorPane.setTopAnchor(onOffPane, 150d);
        
        unManageNode(onOffPane);
    }
    private void initQlockTwoPane() {
        qlockTwo = QlockTwoBuilder.create().prefSize(500, 500).color(QlockTwo.QlockColor.CHERRY_CAKE).language(QlockTwo.Language.ENGLISH).build();
        
        qlockTwoPane = new StackPane(qlockTwo);

        AnchorPane.setLeftAnchor(qlockTwoPane, 20d);
        AnchorPane.setTopAnchor(qlockTwoPane, 50d);
        AnchorPane.setRightAnchor(qlockTwoPane, 20d);

        unManageNode(qlockTwoPane);
    }
    private void initTButtonPane() {
        tButton1 = TButtonBuilder.create().prefSize(100, 100).build();        
                        
        tButtonPane = new StackPane(tButton1);

        AnchorPane.setLeftAnchor(tButtonPane, 150d);
        AnchorPane.setTopAnchor(tButtonPane, 150d);        

        unManageNode(tButtonPane);
    }
    
   
    private void initContentPane() {
        header = new Label("Gauge");
        header.setFont(Fonts.robotoRegular(24));
        header.getStyleClass().add("header");
        header.setMouseTransparent(true);
        AnchorPane.setLeftAnchor(header, 10d);
        AnchorPane.setTopAnchor(header, 10d);

        settingsButton = new Button();
        settingsButton.getStyleClass().add("settings-button");
        settingsButton.setOnAction(event -> animatedOpen(500));
        AnchorPane.setRightAnchor(settingsButton, 10d);
        AnchorPane.setTopAnchor(settingsButton, 10d);
        
        exitButton = new Button();
        exitButton.getStyleClass().add("exit-button");
        exitButton.setOnAction(event -> Platform.exit());
        AnchorPane.setRightAnchor(exitButton, 10d);
        AnchorPane.setBottomAnchor(exitButton, 10d);
        
                        
        contentPane = new AnchorPane();
        contentPane.setPrefSize(SIZE.getWidth(), SIZE.getHeight());
        contentPane.getStyleClass().add("content-background");
        contentPane.getChildren().addAll(header, settingsButton, 
                                         gaugePane, simpleGaugePane, oneEightyGaugePane, simpleRadarChartPane, ledBargraphPane,
                                         ledPane, clockPane, splitFlapPane, lcdClockPane, segmentsPane,
                                         lcdPane, simpleIndicatorPane, roundLcdClockPane, notificationPane,
                                         signalTowerPane, onOffPane, qlockTwoPane, tButtonPane, radialMenuPane, 
                                         exitButton);        
    }
    
    private void initPrefPane() {
        Label header = new Label("Preferences");
        header.setFont(Fonts.robotoRegular(24));
        header.setTextFill(Color.WHITE);
        AnchorPane.setLeftAnchor(header, 10d);
        AnchorPane.setTopAnchor(header, 10d);
                                
        // FlipPanel frontside
        radioButtonGauge            = new RadioButton("Gauge");
        radioButtonSimpleGauge      = new RadioButton("SimpleGauge");
        radioButtonOneEightyGauge   = new RadioButton("OneEightyGauge");
        radioButtonSimpleRadarChart = new RadioButton("SimpleRadarChart");
        radioButtonLedBargraph      = new RadioButton("LedBargraph");

        radioButtonGauge.setOnAction(event -> handleRadioButtonActionEvent(event));
        radioButtonSimpleGauge.setOnAction(event -> handleRadioButtonActionEvent(event));
        radioButtonOneEightyGauge.setOnAction(event -> handleRadioButtonActionEvent(event));
        radioButtonSimpleRadarChart.setOnAction(event -> handleRadioButtonActionEvent(event));
        radioButtonLedBargraph.setOnAction(event -> handleRadioButtonActionEvent(event));

        radioButtonGauge.setSelected(true);
                

        VBox front1ButtonBox = new VBox(radioButtonGauge, radioButtonSimpleGauge, radioButtonOneEightyGauge, radioButtonSimpleRadarChart, radioButtonLedBargraph);
        front1ButtonBox.relocate(10, 0);
        front1ButtonBox.setPrefSize(400, 250);
        front1ButtonBox.setFillWidth(true);
        front1ButtonBox.setSpacing(10);
        front1ButtonBox.setAlignment(Pos.CENTER_LEFT);        

        // FlipPanel backside
        radioButtonLed       = new RadioButton("Led");
        radioButtonClock     = new RadioButton("Clock");
        radioButtonSplitFlap = new RadioButton("SplitFlap");
        radioButtonLcdClock  = new RadioButton("LcdClock");
        radioButtonSegments  = new RadioButton("Segments");

        radioButtonLed.setOnAction(event -> handleRadioButtonActionEvent(event));
        radioButtonClock.setOnAction(event -> handleRadioButtonActionEvent(event));
        radioButtonSplitFlap.setOnAction(event -> handleRadioButtonActionEvent(event));
        radioButtonLcdClock.setOnAction(event -> handleRadioButtonActionEvent(event));
        radioButtonSegments.setOnAction(event -> handleRadioButtonActionEvent(event));
                
        VBox back1ButtonBox = new VBox(radioButtonLed, radioButtonClock, radioButtonSplitFlap, radioButtonLcdClock, radioButtonSegments);
        back1ButtonBox.setPrefSize(400, 250);
        back1ButtonBox.relocate(10, 0);
        back1ButtonBox.setFillWidth(true);
        back1ButtonBox.setSpacing(10);
        back1ButtonBox.setAlignment(Pos.CENTER_LEFT);        
        
        // FlipPanel1        
        Region flipToBack1Button = new Region();
        flipToBack1Button.relocate(10, 10);
        flipToBack1Button.getStyleClass().add("flip-button");
        flipToBack1Button.addEventHandler(MouseEvent.MOUSE_CLICKED, EVENT -> flipPanel1.flipToBack());

        Region flipToFront1Button = new Region();
        flipToFront1Button.relocate(10, 10);
        flipToFront1Button.getStyleClass().add("flip-button");
        flipToFront1Button.addEventHandler(MouseEvent.MOUSE_CLICKED, EVENT -> flipPanel1.flipToFront());

        Pane frontPane1 = new Pane(front1ButtonBox, flipToBack1Button);        
        frontPane1.setMaxSize(front1ButtonBox.getPrefWidth(), front1ButtonBox.getPrefHeight());
        frontPane1.setPadding(new Insets(20, 20, 20, 20));
        frontPane1.getStyleClass().add("panel");
        
        Pane backPane1  = new Pane(back1ButtonBox, flipToFront1Button);
        backPane1.setMaxSize(back1ButtonBox.getPrefWidth(), back1ButtonBox.getPrefHeight());
        backPane1.setPadding(new Insets(20, 20, 20, 20));
        backPane1.getStyleClass().add("panel");
                        
        flipPanel1 = new FlipPanel(Orientation.VERTICAL);
        flipPanel1.getFront().getChildren().add(frontPane1);
        flipPanel1.getBack().getChildren().add(backPane1);

        //flipPanel1.addEventHandler(FlipEvent.FLIP_TO_FRONT_FINISHED, event -> System.out.println("Flip to front finished"));
        //flipPanel1.addEventHandler(FlipEvent.FLIP_TO_BACK_FINISHED, event -> System.out.println("Flip to back finished"));
                
        AnchorPane.setLeftAnchor(flipPanel1, 25d);
        AnchorPane.setTopAnchor(flipPanel1, 50d);        
        
                
        // FlipPanel2 frontside
        radioButtonLcd             = new RadioButton("Lcd");
        radioButtonSimpleIndicator = new RadioButton("SimpleIndicator");
        radioButtonRoundLcdClock   = new RadioButton("RoundLcdClock");
        radioButtonNotification    = new RadioButton("Notification");
        radioButtonRadialMenu      = new RadioButton("RadialMenu");

        radioButtonLcd.setOnAction(event -> handleRadioButtonActionEvent(event));
        radioButtonSimpleIndicator.setOnAction(event -> handleRadioButtonActionEvent(event));
        radioButtonRoundLcdClock.setOnAction(event -> handleRadioButtonActionEvent(event));
        radioButtonNotification.setOnAction(event -> handleRadioButtonActionEvent(event));
        radioButtonRadialMenu.setOnAction(event -> handleRadioButtonActionEvent(event));
        

        VBox front2ButtonBox = new VBox(radioButtonLcd, radioButtonSimpleIndicator, radioButtonRoundLcdClock, radioButtonNotification, radioButtonRadialMenu);
        front2ButtonBox.relocate(10, 0);
        front2ButtonBox.setPrefSize(400, 250);
        front2ButtonBox.setFillWidth(true);
        front2ButtonBox.setSpacing(10);
        front2ButtonBox.setAlignment(Pos.CENTER_LEFT);

        // FlipPanel backside
        radioButtonSignalTower = new RadioButton("SignalTower");
        radioButtonOnOffSwitch = new RadioButton("OnOffSwitch");
        radioButtonQlockTwo    = new RadioButton("QlockTwo");
        radioButtonPushButton  = new RadioButton("PushButton");        

        radioButtonSignalTower.setOnAction(event -> handleRadioButtonActionEvent(event));
        radioButtonOnOffSwitch.setOnAction(event -> handleRadioButtonActionEvent(event));
        radioButtonQlockTwo.setOnAction(event -> handleRadioButtonActionEvent(event));
        radioButtonPushButton.setOnAction(event -> handleRadioButtonActionEvent(event));        

        VBox back2ButtonBox = new VBox(radioButtonSignalTower, radioButtonOnOffSwitch, radioButtonQlockTwo, radioButtonPushButton);
        back2ButtonBox.setPrefSize(400, 250);
        back2ButtonBox.relocate(10, 0);
        back2ButtonBox.setFillWidth(true);
        back2ButtonBox.setSpacing(10);
        back2ButtonBox.setAlignment(Pos.CENTER_LEFT);

        // FlipPanel1        
        Region flipToBack2Button = new Region();
        flipToBack2Button.relocate(10, 10);
        flipToBack2Button.getStyleClass().add("flip-button");
        flipToBack2Button.addEventHandler(MouseEvent.MOUSE_CLICKED, EVENT -> flipPanel2.flipToBack());

        Region flipToFront2Button = new Region();
        flipToFront2Button.relocate(10, 10);
        flipToFront2Button.getStyleClass().add("flip-button");
        flipToFront2Button.addEventHandler(MouseEvent.MOUSE_CLICKED, EVENT -> flipPanel2.flipToFront());

        Pane frontPane2 = new Pane(front2ButtonBox, flipToBack2Button);
        frontPane2.setMaxSize(front2ButtonBox.getPrefWidth(), front2ButtonBox.getPrefHeight());
        frontPane2.setPadding(new Insets(20, 20, 20, 20));
        frontPane2.getStyleClass().add("panel");

        Pane backPane2  = new Pane(back2ButtonBox, flipToFront2Button);
        backPane2.setMaxSize(back2ButtonBox.getPrefWidth(), back2ButtonBox.getPrefHeight());
        backPane2.setPadding(new Insets(20, 20, 20, 20));
        backPane2.getStyleClass().add("panel");

        flipPanel2 = new FlipPanel(Orientation.HORIZONTAL);
        flipPanel2.getFront().getChildren().add(frontPane2);
        flipPanel2.getBack().getChildren().add(backPane2);

        //flipPanel1.addEventHandler(FlipEvent.FLIP_TO_FRONT_FINISHED, event -> System.out.println("Flip to front finished"));
        //flipPanel1.addEventHandler(FlipEvent.FLIP_TO_BACK_FINISHED, event -> System.out.println("Flip to back finished"));

        AnchorPane.setLeftAnchor(flipPanel2, 25d);
        AnchorPane.setBottomAnchor(flipPanel2, 50d);
        
        
        Button close = new Button();
        close.getStyleClass().add("back-button");
        close.setOnAction(event -> {
            preparePaperfold();
            animatedClose(500);
        });
        AnchorPane.setRightAnchor(close, 10d);
        AnchorPane.setTopAnchor(close, 10d);

        toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().addAll(radioButtonGauge, radioButtonSimpleGauge, radioButtonOneEightyGauge, radioButtonSimpleRadarChart, radioButtonLedBargraph,
                                        radioButtonLed, radioButtonClock, radioButtonSplitFlap, radioButtonLcdClock, radioButtonSegments,
                                        radioButtonLcd, radioButtonSimpleIndicator, radioButtonRoundLcdClock, radioButtonNotification, radioButtonRadialMenu,
                                        radioButtonSignalTower, radioButtonOnOffSwitch, radioButtonQlockTwo, radioButtonPushButton);
        
        prefPane = new AnchorPane();
        prefPane.getStyleClass().add("preferences-background");
        prefPane.setPrefSize(PREF_PANE_WIDTH, SIZE.getHeight());
        prefPane.setMaxSize(PREF_PANE_WIDTH, SIZE.getHeight());
        unManageNode(prefPane);        
        
        prefPane.getChildren().addAll(header, flipPanel1, flipPanel2, close);
    }
        
    private void initShadowOverlay() {
        transformLeft  = new PerspectiveTransform();
        transformRight = new PerspectiveTransform();

        leftImage = new ImageView();
        leftImage.setFitWidth(PREF_PANE_WIDTH);
        leftImage.setFitHeight(SIZE.getHeight());
        leftImage.setEffect(transformLeft);
        leftImage.setMouseTransparent(true);

        rightImage = new ImageView();
        rightImage.setFitWidth(PREF_PANE_WIDTH);
        rightImage.setFitHeight(SIZE.getHeight());
        rightImage.setEffect(transformRight);
        rightImage.setMouseTransparent(true);

        shadowRect = new Rectangle(SIZE.getWidth() * 0.45, SIZE.getHeight());
        shadowRect.setFill(Color.BLACK);
        shadowRect.setOpacity(SHADOW_OPACITY);
        shadowRect.setEffect(transformRight);

        shadowOverlay = new Pane();
        shadowOverlay.setPrefSize(0, SIZE.getHeight());
        shadowOverlay.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        shadowOverlay.getChildren().addAll(leftImage, rightImage, shadowRect);
    }

    private void registerListeners(final Scene SCENE) {
        SCENE.setOnMousePressed(mouseEvent -> {
            touchStartX  = mouseEvent.getX();
            mousePressed = true;
            pressedStart = System.currentTimeMillis();
            if (Double.compare(contentPane.getTranslateX(), PREF_PANE_WIDTH) == 0) preparePaperfold();
        });
        SCENE.setOnMouseDragged(mouseEvent -> {
            if (mousePressed) {
                lastTouchX = touchX;
                touchX     = mouseEvent.getX();
                deltaX     = Math.abs(touchX - lastTouchX);
                unManageNode(prefPane);                
                shadowOverlay.setVisible(true);
                if (Double.compare(touchX, lastTouchX) > 0 && contentPane.getTranslateX() < PREF_PANE_WIDTH) {
                    if (Double.compare(contentPane.getTranslateX(), 0) == 0 && deltaX > 20) {
                        animateToCurrentPosition(contentPane.getTranslateX() + deltaX);
                    } else {
                        contentPane.setTranslateX(contentPane.getTranslateX() + deltaX);
                    }
                } else if (Double.compare(touchX, lastTouchX) < 0 && contentPane.getTranslateX() > 0) {
                    if (Double.compare(contentPane.getTranslateX(), PREF_PANE_WIDTH) == 0 && deltaX > 20) {
                        animateToCurrentPosition(contentPane.getTranslateX() - deltaX);
                    } else {
                        contentPane.setTranslateX(contentPane.getTranslateX() - deltaX);
                    }
                }
            }
        });
        SCENE.setOnMouseReleased(mouseEvent -> {
            touchStopX = mouseEvent.getX();
            double deltaX = touchStartX - touchStopX;
            if (deltaX < 0.2 * SIZE.getWidth() && System.currentTimeMillis() - pressedStart > 80) {
                animatedOpen(200);
            } else if (deltaX > -0.2 * SIZE.getWidth() && System.currentTimeMillis() - pressedStart > 80) {
                animatedClose(200);
            }
            mousePressed = false;
        });

        contentPane.translateXProperty().addListener(observable -> {
            double currentX = contentPane.getTranslateX();

            transformLeft.setUlx(0);
            transformLeft.setUly(0);
            transformLeft.setLlx(0);
            transformLeft.setLly(SIZE.getHeight());

            transformLeft.setUrx(currentX * 0.5);
            transformLeft.setUry(INSET - (INSET / PREF_PANE_WIDTH) * currentX);
            transformLeft.setLrx(currentX * 0.5);
            transformLeft.setLry(SIZE.getHeight() - (INSET - (INSET / PREF_PANE_WIDTH) * currentX));

            transformRight.setUlx(currentX * 0.5 - 1); // Move x-coordinate 1px left to avoid a vertical line            
            transformRight.setUly(INSET - (INSET / PREF_PANE_WIDTH) * currentX);
            transformRight.setLlx(currentX * 0.5 - 1); // Move x-coordinate 1px left to avoid a vertical line
            transformRight.setLly(SIZE.getHeight() - (INSET - (INSET / PREF_PANE_WIDTH) * currentX));

            transformRight.setUrx(currentX);
            transformRight.setUry(0);
            transformRight.setLrx(currentX);
            transformRight.setLry(SIZE.getHeight());

            shadowRect.setOpacity(SHADOW_OPACITY - (SHADOW_OPACITY / PREF_PANE_WIDTH) * currentX);

            shadowOverlay.setPrefWidth(currentX);
        });
    }

    private void handleRadioButtonActionEvent(final ActionEvent EVENT) {
        final Object SRC = EVENT.getSource();
        contentPane.getChildren().forEach(node -> unManageNode(node));

        manageNode(header);
        manageNode(settingsButton);
        manageNode(exitButton);

        if (SRC.equals(radioButtonGauge)) {
            manageNode(gaugePane);
            header.setText("Gauge");
        } else if (SRC.equals(radioButtonSimpleGauge)) {
            manageNode(simpleGaugePane);
            header.setText("SimpleGauge");
        } else if (SRC.equals(radioButtonOneEightyGauge)) {
            manageNode(oneEightyGaugePane);
            header.setText("OneEightyGauge");
        } else if (SRC.equals(radioButtonSimpleRadarChart)) {
            manageNode(simpleRadarChartPane);
            header.setText("SimpleRadarChart");
        } else if (SRC.equals(radioButtonLedBargraph)) {
            manageNode(ledBargraphPane);
            header.setText("LedBargraph");
        } else if (SRC.equals(radioButtonLed)) {
            manageNode(ledPane);
            header.setText("Led");
        } else if (SRC.equals(radioButtonClock)) {
            manageNode(clockPane);
            header.setText("Clock");
        } else if (SRC.equals(radioButtonSplitFlap)) {
            manageNode(splitFlapPane);
            header.setText("SplitFlap");
        } else if (SRC.equals(radioButtonLcdClock)) {
            manageNode(lcdClockPane);
            header.setText("LcdClock");
        } else if (SRC.equals(radioButtonSegments)) {
            manageNode(segmentsPane);
            header.setText("Segments");
        } else if (SRC.equals(radioButtonLcd)) {
            manageNode(lcdPane);
            header.setText("Lcd");
        } else if (SRC.equals(radioButtonSimpleIndicator)) {
            manageNode(simpleIndicatorPane);
            header.setText("SimpleIndicator");
        } else if (SRC.equals(radioButtonRoundLcdClock)) {
            manageNode(roundLcdClockPane);
            header.setText("RoundLcdClock");
        } else if (SRC.equals(radioButtonNotification)) {
            manageNode(notificationPane);
            header.setText("Notification");            
        } else if (SRC.equals(radioButtonRadialMenu)) {
            manageNode(radialMenuPane);
            header.setText("RadialMenu");
        } else if (SRC.equals(radioButtonSignalTower)) {
            manageNode(signalTowerPane);
            header.setText("SignalTower");
        } else if (SRC.equals(radioButtonOnOffSwitch)) {
            manageNode(onOffPane);
            header.setText("OnOffTower");
        } else if (SRC.equals(radioButtonQlockTwo)) {
            manageNode(qlockTwoPane);
            header.setText("QlockTwo");
        } else if (SRC.equals(radioButtonPushButton)) {
            manageNode(tButtonPane);
            header.setText("PushButton");
        }
    }
    
    private Section createSection(final double START, final double STOP, final String TEXT) {
        Section section = new Section(START, STOP, TEXT);
        section.setOnEnteringSection(event -> simpleGauge.setTitle(TEXT));
        return section;
    }
    
    private void manageNode(final Node NODE) {
        NODE.setManaged(true);
        NODE.setVisible(true);
    }
    private void unManageNode(final Node NODE) {
        NODE.setVisible(false);
        NODE.setManaged(false);
    }
    

    // ******************** Paperfold animations ******************************
    private void animatedOpen(final double ANIMATION_DURATION) {
        KeyValue kvTranslateX0 = new KeyValue(contentPane.translateXProperty(), contentPane.getTranslateX(), Interpolator.EASE_IN);
        KeyValue kvTranslateX1 = new KeyValue(contentPane.translateXProperty(), PREF_PANE_WIDTH, Interpolator.EASE_OUT);
        KeyFrame kfBegin       = new KeyFrame(Duration.ZERO, kvTranslateX0);
        KeyFrame kfEnd         = new KeyFrame(Duration.millis(ANIMATION_DURATION), kvTranslateX1);
        timeline.getKeyFrames().setAll(kfBegin, kfEnd);
        timeline.play();
        timeline.setOnFinished(event -> {
            manageNode(prefPane);            
            shadowOverlay.setVisible(false);
            preparePaperfold();
        });
    }
    private void animatedClose(final double ANIMATION_DURATION) {
        unManageNode(prefPane);        
        shadowOverlay.setVisible(true);
        KeyValue kvTranslateX0 = new KeyValue(contentPane.translateXProperty(), contentPane.getTranslateX(), Interpolator.EASE_IN);
        KeyValue kvTranslateX1 = new KeyValue(contentPane.translateXProperty(), 0, Interpolator.EASE_OUT);
        KeyFrame kfBegin       = new KeyFrame(Duration.ZERO, kvTranslateX0);
        KeyFrame kfEnd         = new KeyFrame(Duration.millis(ANIMATION_DURATION), kvTranslateX1);
        timeline.getKeyFrames().setAll(kfBegin, kfEnd);
        timeline.play();
        timeline.setOnFinished(event -> {
            shadowOverlay.setVisible(true);
            unManageNode(prefPane);
        });
    }
    private void animateToCurrentPosition(final double CURRENT_X) {
        KeyValue kvTranslateX0 = new KeyValue(contentPane.translateXProperty(), contentPane.getTranslateX(), Interpolator.EASE_IN);
        KeyValue kvTranslateX1 = new KeyValue(contentPane.translateXProperty(), CURRENT_X, Interpolator.EASE_OUT);
        KeyFrame kfBegin       = new KeyFrame(Duration.ZERO, kvTranslateX0);
        KeyFrame kfEnd         = new KeyFrame(Duration.millis(50), kvTranslateX1);
        timeline.getKeyFrames().setAll(kfBegin, kfEnd);
        timeline.play();
    }

    private void preparePaperfold() {
        image       = prefPane.snapshot(new SnapshotParameters(), null);
        pixelReader = image.getPixelReader();
        leftImage.setImage(new WritableImage(pixelReader, 0, 0, (int) (PREF_PANE_WIDTH * 0.5), (int) SIZE.getHeight()));
        rightImage.setImage(new WritableImage(pixelReader, (int) (PREF_PANE_WIDTH * 0.5), 0, (int) (PREF_PANE_WIDTH * 0.5), (int) SIZE.getHeight()));
    }


    // ******************** Application related *******************************
    @Override public void start(Stage stage) {
        notifier = NotifierBuilder.create().build();
        
        Pane pane = new Pane();
        pane.getChildren().addAll(contentPane, prefPane, shadowOverlay);
                
        Scene scene = new Scene(pane, SIZE.getWidth(), SIZE.getHeight());
        scene.setCamera(new PerspectiveCamera());
        scene.getStylesheets().add(Main.class.getResource("styles.css").toExternalForm());
        registerListeners(scene);

        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        
        timer.start();
    }

    @Override public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

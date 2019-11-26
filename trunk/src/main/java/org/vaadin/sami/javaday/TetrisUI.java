package org.vaadin.sami.javaday;

import com.vaadin.event.ShortcutAction;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.*;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import org.tepi.imageviewer.ImageViewer;
import org.vaadin.hezamu.canvas.Canvas;
import org.vaadin.sami.rk7.Config;
import org.vaadin.sami.tetris.Game;
import org.vaadin.sami.tetris.Grid;
import org.vaadin.sami.tetris.Tetromino;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;

import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Notification.Type;

import javax.imageio.ImageIO;
import javax.servlet.annotation.WebServlet;
import javax.swing.filechooser.FileSystemView;

import org.vaadin.viritin.button.PrimaryButton;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import com.vaadin.ui.Label;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.vaadin.sami.rk7.GetFailTestFromSystem.showChildrenRes;
import static org.vaadin.sami.rk7.Utils.deleteAllFilesFolder;

@Push
@Theme("valo")
@Title("Vaadin Tetris")
public class TetrisUI extends UI {

    @WebServlet(value = {"/*"}, asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = TetrisUI.class, resourceCacheTime = 0)
    public static class Servlet extends VaadinServlet {
    }

    private static final int PAUSE_TIME_MS = 500;

    private static final long serialVersionUID = -152735180021558969L;

    // Tile size in pixels
    protected static final int TILE_SIZE = 30;

    // Playfield width in tiles
    private static final int PLAYFIELD_W = 10;

    // Playfield height in tiles
    private static final int PLAYFIELD_H = 20;

    // Playfield background color
    private static final String PLAYFIELD_COLOR = "#000";

    private VerticalLayout layout;
    private VerticalLayout imageLayout;
    private Canvas canvas;
    protected boolean running;
    protected Game game;

    private Label scoreLabel;

    //---------RK7---------
    public static FileSystemView fileSystemView;
    public static ArrayList<File> listFile = new ArrayList<>();
    public static TextField resultDirPath;
    public static Map<String, String> difImg = new HashMap<>();
    public static Map<String, String> ERROR_DIFF_IMG = new HashMap<>();
    public static Map<String, String> ERROR_TEST = new HashMap<>();

    public static TextField pathProject;
    private static ImageViewer imageViewer;
    private TextField selectedImage = new TextField();

    public static String SELECTED_ER_T_IN_ALL;
    public static String SELECTED_ER_T_IN_SEL;

    public static Resource selectDeffImg;

    public static String basepath = VaadinService.getCurrent()
            .getBaseDirectory().getAbsolutePath();

    //---------------------

    @Override
    protected void init(VaadinRequest request) {
        // Find the application directory
        // Инициализируем конфиги
        new Config();

        layout = new VerticalLayout();
//        layout.setSizeFull();
        layout.setSizeUndefined();
        layout.setSpacing(true);
        layout.setMargin(true);
        setContent(layout);

        layout.addComponent(new About());

        //*** RK7 *****************************************************************
        //--- РАСПОЛОЖЕНИЕ (горизонтальная панель)
        VerticalLayout btnPanelListT = new VerticalLayout();
        btnPanelListT.addStyleName("outlined");
        btnPanelListT.setSpacing(false);
        btnPanelListT.setMargin(false);
//        btnPanelListT.setSizeFull();

        //--- РАСПОЛОЖЕНИЕ (горизонтальная панель)
        HorizontalLayout settingsPanel = new HorizontalLayout();
        settingsPanel.addStyleName("outlined");
        settingsPanel.setSpacing(false);
        settingsPanel.setMargin(false);
//        settingsPanel.setSizeFull();

        //--- РАСПОЛОЖЕНИЕ (вертикальная панель)
        HorizontalLayout generalPanel = new HorizontalLayout();
        generalPanel.setMargin(true);
        generalPanel.setSpacing(true);
        generalPanel.addStyleName("outlined");
        generalPanel.setHeight(100.0f, Unit.PERCENTAGE);
//        layout.setComponentAlignment(generalPanel, Alignment.BOTTOM_RIGHT);

        //*********************ИЗОБРАЖЕНИЯ ******************************
//        Label info = new Label(
//                "<b>ImageViewer Demo Application</b>&nbsp;&nbsp;&nbsp;"
//                        + "<i>Try the arrow keys, space/enter and home/end."
//                        + " You can also click on the pictures or use the " + "mouse wheel.&nbsp;&nbsp;",
//                ContentMode.HTML);

        imageViewer = new ImageViewer();
        imageViewer.setWidth("400");
        imageViewer.setHeight("500");
//        imageViewer.setSizeFull();
        imageViewer.setImages(createImageList());
        imageViewer.setAnimationEnabled(false);
        imageViewer.setSideImageRelativeWidth(0.7f);

        imageViewer.addListener((ImageViewer.ImageSelectionListener) e -> {
            selectedImage.setValue(e.getSelectedImageIndex() >= 0 ? String.valueOf(e.getSelectedImageIndex()) : "-");
        });
        HorizontalLayout hl = new HorizontalLayout();
        hl.setSizeUndefined();
        hl.setMargin(false);
        hl.setSpacing(true);
//        hl.addComponent(info);
//        generalPanel.addComponent(hl);
//        generalPanel.addComponent(imageViewer);
//        generalPanel.setExpandRatio(imageViewer, 1);
//        generalPanel.setComponentAlignment(imageViewer, Alignment.BOTTOM_RIGHT);
//        layout.addComponent(hl);
//        layout.addComponent(imageViewer);
//        layout.setExpandRatio(imageViewer, 1);
//
        Layout ctrls = createControls();
//        layout.addComponent(ctrls);
//        layout.setComponentAlignment(ctrls, Alignment.BOTTOM_CENTER);

//        Label images = new Label("Sample Photos: Bruno Monginoux / www.Landscape-Photo.net (cc-by-nc-nd)");
//        images.setSizeUndefined();
//        images.setStyleName("licence");
//        layout.addComponent(images);
//        layout.setComponentAlignment(images, Alignment.BOTTOM_RIGHT);

        setContent(layout);
        imageViewer.setCenterImageIndex(0);
        imageViewer.focus();

        // Serve the image from the theme
//        Resource res = new FileResource(
//                new File(basepath + "\\WEB-INF\\images\\difference_scr1.png"));
//        Resource res2 = new FileResource(
//                new File("D:\\TestingResult_19.11.2019_01.03.56\\50805\\Screens\\difference_scr1.png"));
//
//        // Display the image without caption
//        Image image = new Image();
//        image.setHeight("500");
//        image.setWidth("600");
//        image.setSource(res);

//        generalPanel.addComponent(image);
//        generalPanel.setComponentAlignment(image, Alignment.BOTTOM_RIGHT);

        //********* ЗДЕСЬ БУДЕТ ВЫГРУЗКА ПАПОК В СПИСОК *****************
        ListSelect listAllFailTest = new ListSelect<>("Все упавшие тесты");
        listAllFailTest.setRows(6);
//        listAllFailTest.setWidth(100.0f, Unit.PERCENTAGE);
        listAllFailTest.setWidth("170");
        listAllFailTest.setHeight("525");
//        generalPanel.addComponent(listAllFailTest);
//        generalPanel.setComponentAlignment(listAllFailTest, Alignment.BOTTOM_LEFT);

        Set<String> eList = new LinkedHashSet<>();

        listAllFailTest.addValueChangeListener(event -> {
                SELECTED_ER_T_IN_ALL = event.getValue().toString()
                 .replace("[","").replace("]","");
            System.out.println("|" + SELECTED_ER_T_IN_ALL + "|");
            System.out.println(difImg.get(SELECTED_ER_T_IN_ALL.trim()));

            selectDeffImg = new FileResource(
                    new File(difImg.get(SELECTED_ER_T_IN_ALL.trim())));
            List<Resource> img = new ArrayList<Resource>();
//            img.add(selectDeffImg);

//            imageViewer.setImages(img);
//            imageViewer.focus();
            try {
                deleteAllFilesFolder(basepath + "/WEB-INF/images/view"); //очистим папку с изображениями
                BufferedImage bufferedImage = ImageIO.read(new File(difImg.get(SELECTED_ER_T_IN_ALL.trim())));
                //сохранение на сервере
                ImageIO.write(bufferedImage, "png",
                        new File(basepath + "/WEB-INF/images/view/" + SELECTED_ER_T_IN_ALL + ".png"));
                String filePath = basepath + "/WEB-INF/images/view/" + SELECTED_ER_T_IN_ALL + ".png";
                Resource resource = new FileResource(
                        new File(filePath));
                img.add(resource);
                imageViewer.setImages(img);
                imageViewer.focus();
            } catch (IOException e) {
                e.printStackTrace();
            }
//            //сохранение на сервере
//            ImageIO.write(, "png",
//                    new File(basepath + "Images/" + SELECTED_ER_T_IN_ALL + ".png"));
//            image.setSource(selectDeffImg);
        });

        ListSelect listSelectFailTest = new ListSelect<>("Выбранные для отладки");
        listAllFailTest.setRows(6);
        listSelectFailTest.setWidth("170");
        listSelectFailTest.setHeight("525");
        generalPanel.addComponent(listSelectFailTest);
        generalPanel.setComponentAlignment(listSelectFailTest, Alignment.BOTTOM_RIGHT);
        //--------------------------------------------
        listSelectFailTest.addValueChangeListener(event -> {
            SELECTED_ER_T_IN_SEL = event.getValue().toString()
                    .replace("[","").replace("]","");
        });

        fileSystemView = FileSystemView.getFileSystemView();

        TwinColSelect<String> listFailTest = new TwinColSelect<>();
        listFailTest.setWidth("325");
        listFailTest.setHeight("525");
        showChildrenRes("D:\\TestingResult_19.11.2019_01.03.56");
        listFailTest.setRightColumnCaption("Для отладки");
        listFailTest.setLeftColumnCaption("Все упавшие тесты");

        // Handle value changes
        listFailTest.addSelectionListener(event -> {
                layout.addComponent(
                        new Label("Selected: " + event.getNewSelection()));
        });


//        generalPanel.addComponent(listFailTest);
//        generalPanel.setComponentAlignment(listFailTest, Alignment.BOTTOM_LEFT);
//
//        generalPanel.addComponent(image);
//        generalPanel.setComponentAlignment(image, Alignment.BOTTOM_RIGHT);


        // КНОПКИ
        Button btnUploadFTest = new Button("Загрузить fail-тесты");
        btnUploadFTest.addClickListener(event -> {
//            System.out.println("123");
            showChildrenRes("D:\\TestingResult_19.11.2019_01.03.56");
            // запоним список fail-тетсов
            ArrayList<String> s = new ArrayList<>();

//            Resource res3 = new FileResource(
//                    new File("D:\\PROJECT\\GeneralProjects\\RK7Debug\\trunk\\src\\main\\resources\\cat-1-bw.jpg"));
//            image.setSource(res3);
//            listFile.forEach(x -> s.add(x.getName()));
//            listFailTest.setItems(s);
            try {
                for (Map.Entry<String, String> fTest : ERROR_TEST.entrySet()) {
                    s.add(fTest.getKey());
                }
//                listFailTest.setItems(s);
                listAllFailTest.setItems(s);
                Notification.show("Загрузка успешно завершена!", Type.TRAY_NOTIFICATION);
            }catch (Exception e) {
                Notification.show("Произошла ошибка! \n" +e, Type.ERROR_MESSAGE);
            }

        });

        Button btnChooseResultDir = new Button("выбрать");
        btnChooseResultDir.addClickListener(event -> {
            Notification.show("The button was clicked", Type.TRAY_NOTIFICATION);
//            image.setSource(res2);
//            System.out.println("OPEN IMG " + difImg.get(SELECTED_ER_T_IN_ALL.trim()));
//            selectDeffImg = new FileResource(
//                    new File(difImg.get(SELECTED_ER_T_IN_ALL.trim())));
//            Resource res1 = new FileResource(
//                    new File("D:\\PROJECT\\GeneralProjects\\RK7Debug\\trunk\\src\\main\\resources\\cat-1.jpg"));
//            image.setSource(res1);
        });

        Button btnAdd = new Button(">");
        btnAdd.setIconAlternateText(">");

        btnAdd.addClickListener(event -> {
            Notification.show("The button was clicked", Type.TRAY_NOTIFICATION);
            eList.add(SELECTED_ER_T_IN_ALL);
            listSelectFailTest.setItems(eList);
        });
        Button btnDel = new Button("<");
        btnDel.setIconAlternateText("<");

        btnDel.addClickListener(event -> {
            Notification.show("The button was clicked", Type.TRAY_NOTIFICATION);
            eList.remove(SELECTED_ER_T_IN_SEL);
            listSelectFailTest.setItems(eList);
        });
        //-##########################################################################


        //-##########################################################################

        // ПОЛЯ
        resultDirPath = new TextField();
        resultDirPath.setPlaceholder("Write something");
//        resultDirPath.setMaxLength(10);

        pathProject = new TextField();
        pathProject.setPlaceholder("Укажите путь к папке 'input' в проекте");
        pathProject.setWidth("350");
//        pathProject.setMaxLength(15);

        /// ПОСТРОЕНИЕ ИНТЕРФЕЙСА (РАСПОЛОЖЕНИЕ ЭЕЛЕМЕНТОВ)

        layout.addComponent(settingsPanel);
        layout.addComponent(generalPanel);

        settingsPanel.addComponent(btnUploadFTest); // кнопка Загрузить fail-тесты
        settingsPanel.setComponentAlignment(btnUploadFTest, Alignment.BOTTOM_LEFT);

        settingsPanel.addComponent(resultDirPath);

        generalPanel.addComponent(listAllFailTest);
        generalPanel.setComponentAlignment(listAllFailTest, Alignment.BOTTOM_LEFT);

        btnPanelListT.addComponent(btnAdd);
        btnPanelListT.setComponentAlignment(btnAdd, Alignment.MIDDLE_CENTER);
        btnPanelListT.addComponent(btnDel);
        btnPanelListT.setComponentAlignment(btnAdd, Alignment.MIDDLE_CENTER);
        generalPanel.addComponent(btnPanelListT);
        generalPanel.setComponentAlignment(btnPanelListT, Alignment.MIDDLE_CENTER);

//        generalPanel.addComponent(btnAdd);
//        generalPanel.setComponentAlignment(btnAdd, Alignment.MIDDLE_CENTER);

        generalPanel.addComponent(listSelectFailTest);
        generalPanel.setComponentAlignment(listSelectFailTest, Alignment.BOTTOM_RIGHT);

//        generalPanel.addComponent(listFailTest);
//        generalPanel.setComponentAlignment(listFailTest, Alignment.BOTTOM_LEFT);

        generalPanel.addComponent(hl);
        generalPanel.addComponent(imageViewer);
        generalPanel.setExpandRatio(imageViewer, 1);
        generalPanel.setComponentAlignment(imageViewer, Alignment.BOTTOM_RIGHT);
        layout.addComponent(ctrls);
        layout.setComponentAlignment(ctrls, Alignment.BOTTOM_CENTER);

//        generalPanel.addComponent(image);
//        generalPanel.setComponentAlignment(image, Alignment.BOTTOM_RIGHT);

        settingsPanel.addComponent(btnChooseResultDir);
        settingsPanel.addComponent(pathProject);
        //---------------

        setContent(layout);

        // **************************************************************************
        // ДАЛЬШЕ ТЕТРИС ------------------------------------------------------------

        // Button for moving left
        final Button leftBtn = new Button(VaadinIcons.ARROW_LEFT);
        leftBtn.addClickListener(e -> {
            game.moveLeft();
            drawGameState();
        });
        leftBtn.setClickShortcut(KeyCode.ARROW_LEFT);

        // Button for moving right
        final Button rightBtn = new Button(VaadinIcons.ARROW_RIGHT);
        rightBtn.addClickListener(e -> {
            game.moveRight();
            drawGameState();

        });
        rightBtn.setClickShortcut(KeyCode.ARROW_RIGHT);

        // Button for rotating clockwise
        final Button rotateCWBtn = new Button("[key down]",
                VaadinIcons.ROTATE_RIGHT);
        rotateCWBtn.addClickListener(e -> {
            game.rotateCW();
            drawGameState();
        });
        rotateCWBtn.setClickShortcut(KeyCode.ARROW_DOWN);

        // Button for rotating counter clockwise
        final Button rotateCCWBtn = new Button("[key up]",
                VaadinIcons.ROTATE_LEFT);
        rotateCCWBtn.addClickListener(e -> {
            game.rotateCCW();
            drawGameState();
        });
        rotateCCWBtn.setClickShortcut(KeyCode.ARROW_UP);

        // Button for dropping the piece
        final Button dropBtn = new Button("[space]", VaadinIcons.ARROW_DOWN);
        dropBtn.addClickListener(e -> {
            game.drop();
            drawGameState();
        });
        dropBtn.setClickShortcut(KeyCode.SPACEBAR);

        // Button for restarting the game
        final Button restartBtn = new PrimaryButton().withIcon(VaadinIcons.PLAY);
        restartBtn.addClickListener(e -> {
            running = !running;
            if (running) {
                game = new Game(10, 20);
                startGameThread();
                restartBtn.setIcon(VaadinIcons.STOP);
                dropBtn.focus();
            } else {
                restartBtn.setIcon(VaadinIcons.PLAY);
                gameOver();
            }
        });

        layout.addComponent(new MHorizontalLayout(
                restartBtn, leftBtn, rightBtn, rotateCCWBtn, rotateCWBtn,
                dropBtn
        ));

        // Canvas for the game
        canvas = new Canvas();
        layout.addComponent(canvas);
        canvas.setWidth((TILE_SIZE * PLAYFIELD_W) + "px");
        canvas.setHeight((TILE_SIZE * PLAYFIELD_H) + "px");
		// canvas.setBackgroundColor(PLAYFIELD_COLOR);

        // Label for score
        scoreLabel = new Label("");
        layout.addComponent(scoreLabel);

    }

    /// IMAGE VIEWER
    private Layout createControls() {
        HorizontalLayout hl = new HorizontalLayout();
        hl.setSizeUndefined();
        hl.setMargin(false);
        hl.setSpacing(true);

        CheckBox c = new CheckBox("HiLite");
        c.addValueChangeListener(e -> {
            imageViewer.setHiLiteEnabled(e.getValue());
            imageViewer.focus();
        });

        c.setValue(true);
        hl.addComponent(c);
        hl.setComponentAlignment(c, Alignment.BOTTOM_CENTER);

        c = new CheckBox("Animate");
        c.addValueChangeListener(e -> {
            imageViewer.setAnimationEnabled(e.getValue());
            imageViewer.focus();
        });
        c.setValue(true);
        hl.addComponent(c);
        hl.setComponentAlignment(c, Alignment.BOTTOM_CENTER);

        Slider s = new Slider("Animation duration (ms)");
        s.setMax(2000);
        s.setMin(200);
        s.setWidth("120px");
        s.addValueChangeListener(e -> {
            imageViewer.setAnimationDuration((int) Math.round(e.getValue()));
            imageViewer.focus();
        });
        s.setValue(350d);
        hl.addComponent(s);
        hl.setComponentAlignment(s, Alignment.BOTTOM_CENTER);

        s = new Slider("Center image width");
        s.setResolution(2);
        s.setMax(1);
        s.setMin(0.1);
        s.setWidth("120px");
        s.addValueChangeListener(e -> {
            imageViewer.setCenterImageRelativeWidth(e.getValue().floatValue());
            imageViewer.focus();
        });
        s.setValue(0.55);
        hl.addComponent(s);
        hl.setComponentAlignment(s, Alignment.BOTTOM_CENTER);

        s = new Slider("Side image count");
        s.setMax(5);
        s.setMin(1);
        s.setWidth("120px");

        s.addValueChangeListener(e -> {
            imageViewer.setSideImageCount((int) Math.round(e.getValue()));
            imageViewer.focus();
        });
        s.setValue(2d);
        hl.addComponent(s);
        hl.setComponentAlignment(s, Alignment.BOTTOM_CENTER);

        s = new Slider("Side image width");
        s.setResolution(2);
        s.setMax(0.8);
        s.setMin(0.5);
        s.setWidth("120px");

        s.addValueChangeListener(e -> {
            imageViewer.setSideImageRelativeWidth(e.getValue().floatValue());
            imageViewer.focus();
        });

        s.setValue(0.65);
        hl.addComponent(s);
        hl.setComponentAlignment(s, Alignment.BOTTOM_CENTER);

        s = new Slider("Horizontal padding");
        s.setMax(10);
        s.setMin(0);
        s.setWidth("120px");

        s.addValueChangeListener(e -> {
            imageViewer.setImageHorizontalPadding((int) Math.round(e.getValue()));
            imageViewer.focus();
        });
        s.setValue(1d);
        hl.addComponent(s);
        hl.setComponentAlignment(s, Alignment.BOTTOM_CENTER);

        s = new Slider("Vertical padding");
        s.setMax(10);
        s.setMin(0);
        s.setWidth("120px");
        s.addValueChangeListener(e -> {
            imageViewer.setImageVerticalPadding((int) Math.round(e.getValue()));
            imageViewer.focus();
        });
        s.setValue(5d);
        hl.addComponent(s);
        hl.setComponentAlignment(s, Alignment.BOTTOM_CENTER);

        selectedImage.setWidth("50px");
        hl.addComponent(selectedImage);
        hl.setComponentAlignment(selectedImage, Alignment.BOTTOM_CENTER);

        return hl;
    }

    /**
     * Creates a list of Resources to be shown in the ImageViewer.
     *
     * @return List of Resource instances
     */
    protected List<Resource> createImageList() {
        List<Resource> img = new ArrayList<Resource>();
//        for (int i = 1; i < 10; i++) {
//            img.add(new ThemeResource("images/" + i + ".jpg"));
//        }
        Resource res = new FileResource(
                new File(basepath + "/rk7_logo.jpg"));
        img.add(res);
        return img;
    }
    //IMAGE VIEWER END

    /**
     * Start the game thread that updates the game periodically.
     */
    protected synchronized void startGameThread() {
        Thread t = new Thread() {
            public void run() {

                // Continue until stopped or game is over
                while (running && !game.isOver()) {

                    drawGameState();

                    // Pause for a while
                    try {
                        sleep(PAUSE_TIME_MS);
                    } catch (InterruptedException igmored) {
                    }

                    // Step the game forward and update score
                    game.step();
                    updateScore();

                }

                // Notify user that game is over
                gameOver();

            }
        };
        t.start();

    }

    /**
     * Update the score display.
     */
    protected synchronized void updateScore() {
        access(() -> {
            scoreLabel.setValue("Score: " + game.getScore());
        });
    }

    /**
     * Quit the game.
     */
    protected synchronized void gameOver() {
        running = false;
        // Draw the state
        access(() -> {
            Notification.show("Game Over", "Your score: " + game.getScore(),
                    Type.HUMANIZED_MESSAGE);
        });
    }

    /**
     * Draw the current game state.
     */
    protected synchronized void drawGameState() {

        // Draw the state
        access(() -> {

            // Reset and clear canvas
            canvas.clear();
            canvas.setFillStyle(PLAYFIELD_COLOR);
            canvas.fillRect(0, 0, game.getWidth() * TILE_SIZE + 2, game.getHeight()
                    * TILE_SIZE + 2);

            // Draw the tetrominoes
            Grid state = game.getCurrentState();
            for (int x = 0; x < state.getWidth(); x++) {
                for (int y = 0; y < state.getHeight(); y++) {

                    int tile = state.get(x, y);
                    if (tile > 0) {

                        String color = Tetromino.get(tile).getColor();
                        canvas.setFillStyle(color);
                        canvas.fillRect(x * TILE_SIZE + 1, y * TILE_SIZE + 1,
                                TILE_SIZE - 2, TILE_SIZE - 2);
                    }
                }
            }
        });
    }

}

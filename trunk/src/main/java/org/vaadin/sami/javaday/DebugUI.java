package org.vaadin.sami.javaday;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.*;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
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

import javax.servlet.annotation.WebServlet;
import javax.swing.filechooser.FileSystemView;

import org.vaadin.viritin.button.PrimaryButton;
import org.vaadin.viritin.layouts.MHorizontalLayout;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.vaadin.sami.rk7.Utils.*;
import static org.vaadin.sami.rk7.GetFailTestFromSystem.showChildrenRes;

@Push
@Theme("valo")
@Title("RK 7 Debug")
public class DebugUI extends UI {

    @WebServlet(value = {"/*"}, asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DebugUI.class, resourceCacheTime = 0)
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
    private Canvas canvas;
    protected boolean running;
    protected Game game;

    private Label scoreLabel;

    //---------RK7---------
    public static FileSystemView fileSystemView;
    public static ArrayList<File> listFile = new ArrayList<>();
    public static Map<String, String> difImg = new HashMap<>();
    public static Map<String, String> ERROR_DIFF_IMG = new HashMap<>();
    public static Map<String, String> ERROR_TEST = new HashMap<>();
    public static Map<String, Map<Integer, String>> ALL_IMG_IN_FAIL_TEST = new HashMap<>();
    public static Set<String> eList = new LinkedHashSet<>();
    public static Set<String> LIST_RESULT_DIR = new LinkedHashSet<>();
    public static List<String> LIST_PROJECT_DIR = new ArrayList<>();
    public static String SELECTED_RESULT_DIR = null;
    public static String SELECTED_PROJECT_DIR = null;

    public static Label log;
    private static ImageViewer imageViewer;
    private TextField selectedImage = new TextField();
    public static NativeSelect inputDirResult;
    public static NativeSelect inputDirProject;

    public static String SELECTED_ER_T_IN_ALL;
    public static String SELECTED_ER_T_IN_SEL;

    public static Resource selectDeffImg;

    public static String basepath = VaadinService.getCurrent()
            .getBaseDirectory().getAbsolutePath();

    //---------------------

    @Override
    protected void init(VaadinRequest request) {
        new Config(); // Инициализируем конфиги

        // Очищаем все коллекции
        listFile.clear();
        difImg.clear();
        ERROR_DIFF_IMG.clear();
        ERROR_TEST.clear();
        ALL_IMG_IN_FAIL_TEST.clear();
        eList.clear();
        LIST_RESULT_DIR.clear();
        LIST_PROJECT_DIR.clear();

        getResultDir(); // Заполняем список директорий результатов тестов

        layout = new VerticalLayout();
        layout.setSizeUndefined();
        layout.setSpacing(true);
        layout.setMargin(true);
        setContent(layout);

        layout.addComponent(new About()); // о программе

        //--- РАСПОЛОЖЕНИЕ (панель кнопок главной таблицы)
        VerticalLayout btnGTable = new VerticalLayout();
        btnGTable.addStyleName("outlined");
        btnGTable.setSpacing(false);
        btnGTable.setMargin(false);

        //--- РАСПОЛОЖЕНИЕ (панель настроек)
        HorizontalLayout settingsPanel = new HorizontalLayout();
        settingsPanel.addStyleName("outlined");
        settingsPanel.setSpacing(true);
        settingsPanel.setMargin(false);

        //--- РАСПОЛОЖЕНИЕ (вертикальная панель)
        HorizontalLayout GPanel = new HorizontalLayout();
        GPanel.setMargin(true);
        GPanel.setSpacing(true);
        GPanel.addStyleName("outlined");
        GPanel.setHeight(100.0f, Unit.PERCENTAGE);

        //*********************ИЗОБРАЖЕНИЯ ******************************
        // Просмотрщик изображений
        imageViewer = new ImageViewer();
        imageViewer.setWidth("920");
        imageViewer.setHeight("530");

//        imageViewer.setSizeFull();
        imageViewer.setImages(createImageList());
        imageViewer.setAnimationEnabled(false);
        imageViewer.setSideImageRelativeWidth(0.7f);

        imageViewer.addListener((ImageViewer.ImageSelectionListener) e -> {
            selectedImage.setValue(e.getSelectedImageIndex() >= 0 ? String.valueOf(e.getSelectedImageIndex()) : "-");
        });

        // информация к просмотрщику
        Label info = new Label("<div style = 'margin: -45px 0px 45px 100px;'><i>*от&nbsp;</i>"
                + "<b>difference_scr</b>"
                + "&nbsp;<u>слева эталон</u> с проекта, а <u>справа скрин</u> с кассы.</div>",
                ContentMode.HTML);
        info.setVisible(false);

        // Панель управления для просмотрщика
        Layout ctrls = createControls();

        setContent(layout);
        try {
            imageViewer.setCenterImageIndex(0);
        } catch (Exception ignored) {
        }
        imageViewer.focus();

        // Левая таблица павших тестов (все павшие тесты)
        ListSelect listAllFailTest = new ListSelect<>("Все упавшие тесты");
        listAllFailTest.setRows(6);
        listAllFailTest.setWidth("170");
        listAllFailTest.setHeight("525");

        listAllFailTest.addValueChangeListener(event -> {
            SELECTED_ER_T_IN_ALL = event.getValue().toString()
                    .replace("[", "").replace("]", "");

            selectDeffImg = new FileResource(new File(difImg.get(SELECTED_ER_T_IN_ALL.trim())));
            deleteAllFilesFolder(basepath + "/WEB-INF/images/view"); //очистим папку с изображениями
            imageViewer.setImages(getResForImageViewer());
            imageViewer.focus();
        });

        ListSelect listSelectFailTest = new ListSelect<>("Выбранные для отладки");
        listAllFailTest.setRows(6);
        listSelectFailTest.setWidth("170");
        listSelectFailTest.setHeight("525");
        //--------------------------------------------
        listSelectFailTest.addValueChangeListener(event -> {
            SELECTED_ER_T_IN_SEL = event.getValue().toString()
                    .replace("[", "").replace("]", "");
        });

        fileSystemView = FileSystemView.getFileSystemView();

        // КНОПКИ
        // кнопка Загрузить fail-тесты
        Button btnUploadFTest = new Button("Загрузить fail-тесты");
        btnUploadFTest.addClickListener(event -> {
            try {
                listAllFailTest.clear();
            }catch (Exception ignored) {}
            refreshLog();
            if (SELECTED_RESULT_DIR != null) {
                showChildrenRes(SELECTED_RESULT_DIR);
                // запоним список fail-тетсов
                ArrayList<String> s = new ArrayList<>();

                try {
                    for (Map.Entry<String, String> fTest : ERROR_TEST.entrySet()) {
                        s.add(fTest.getKey());
                    }
                    listAllFailTest.setItems(s);
                    Notification.show("Загрузка успешно завершена!", Type.TRAY_NOTIFICATION);
                } catch (Exception e) {
                    Notification.show("Произошла ошибка! \n" + e, Type.ERROR_MESSAGE);
                }
                info.setVisible(true);
            } else {
                Notification.show("Не выбрана директория результатов прогона!", Type.ERROR_MESSAGE);
            }
        });

        // Кнопка ЗАМЕНИТЬ
        Button btnChooseResultDir = new Button("ЗАМЕНИТЬ");
        btnChooseResultDir.setStyleName(ValoTheme.BUTTON_DANGER);
        btnChooseResultDir.addClickListener(event -> {
            refreshLog();
            if (ERROR_DIFF_IMG.size() >= 1
                    && SELECTED_RESULT_DIR != null
                    && SELECTED_PROJECT_DIR != null) {
                try {
                    reReference();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Notification notification = new Notification("Не все поля заполнены!",
                        "");
                notification.show(Page.getCurrent());
            }
        });

        // Кнопка >
        Button btnAdd = new Button(">");
        btnAdd.setIconAlternateText(">");

        btnAdd.addClickListener(event -> {
            eList.add(SELECTED_ER_T_IN_ALL);
            listSelectFailTest.setItems(eList);
            ERROR_DIFF_IMG.put(SELECTED_ER_T_IN_ALL, difImg.get(SELECTED_ER_T_IN_ALL));
        });

        // Кнопка <
        Button btnDel = new Button("<");
        btnDel.setIconAlternateText("<");

        btnDel.addClickListener(event -> {
            eList.remove(SELECTED_ER_T_IN_SEL);
            ERROR_DIFF_IMG.remove(SELECTED_ER_T_IN_SEL);
            listSelectFailTest.setItems(eList);
        });

        // ПОЛЯ

        // Лог выполнения замены
        log = new Label();
        log.setWidth("700");
        log.setContentMode(ContentMode.HTML);
        refreshLog();

        // Поле выбора директории результатов прогона
        inputDirResult = new NativeSelect<>();
        inputDirResult.clear();
        inputDirResult.setItems(LIST_RESULT_DIR);
        inputDirResult.setEmptySelectionAllowed(false);
        inputDirResult.setDescription("Директория результатов тестового прогона");

        inputDirResult.addValueChangeListener(event -> {
            Notification.show("Выбрано:",
                    String.valueOf(event.getValue()),
                    Type.TRAY_NOTIFICATION);
            try {
                SELECTED_RESULT_DIR = event.getValue().toString();
            } catch (NullPointerException e) {
                SELECTED_RESULT_DIR = null;
            }
        });

        // Поле выбора проекта
        inputDirProject = new NativeSelect<>();
        inputDirProject.clear();
        LIST_PROJECT_DIR.clear();
        // Данные
        LIST_PROJECT_DIR.add("msk");
        LIST_PROJECT_DIR.add("msk-sea");
        LIST_PROJECT_DIR.add("vrn");
        inputDirProject.setItems(LIST_PROJECT_DIR);
        inputDirProject.setEmptySelectionAllowed(false);
        inputDirProject.setDescription("Директория эталонов для тестов в проекте");

        inputDirProject.addValueChangeListener(event -> {
            Notification.show("Выбрано:",
                    String.valueOf(event.getValue()),
                    Type.TRAY_NOTIFICATION);
            try {
                SELECTED_PROJECT_DIR = event.getValue().toString();
            } catch (NullPointerException e) {
                SELECTED_PROJECT_DIR = null;
            }
        });

        // Разделитель между кнопкой "Загрузить.." и выбором директории проекта
        Image image = new Image();
        Resource resource = new FileResource(new File(basepath + "/burger.png"));
        image.setWidth("30");
        image.setSource(resource);

        /// ПОСТРОЕНИЕ ИНТЕРФЕЙСА (РАСПОЛОЖЕНИЕ ЭЕЛЕМЕНТОВ)

        layout.addComponent(settingsPanel);
        layout.addComponent(GPanel);

        settingsPanel.addComponent(inputDirResult);

        settingsPanel.addComponent(btnUploadFTest); // кнопка Загрузить fail-тесты
        settingsPanel.setComponentAlignment(btnUploadFTest, Alignment.BOTTOM_LEFT);

        GPanel.addComponent(listAllFailTest);
        GPanel.setComponentAlignment(listAllFailTest, Alignment.BOTTOM_LEFT);

        btnGTable.addComponent(btnAdd);
        btnGTable.setComponentAlignment(btnAdd, Alignment.MIDDLE_CENTER);
        btnGTable.addComponent(btnDel);
        btnGTable.setComponentAlignment(btnAdd, Alignment.MIDDLE_CENTER);
        GPanel.addComponent(btnGTable);
        GPanel.setComponentAlignment(btnGTable, Alignment.MIDDLE_CENTER);

        GPanel.addComponent(listSelectFailTest);
        GPanel.setComponentAlignment(listSelectFailTest, Alignment.BOTTOM_RIGHT);

        GPanel.addComponent(imageViewer);
        GPanel.setExpandRatio(imageViewer, 1);
        GPanel.setComponentAlignment(imageViewer, Alignment.BOTTOM_RIGHT);
        layout.addComponent(info);
        layout.setComponentAlignment(info, Alignment.BOTTOM_CENTER);
        layout.addComponent(ctrls);
        layout.setComponentAlignment(ctrls, Alignment.BOTTOM_CENTER);

        settingsPanel.addComponents(image);
        settingsPanel.addComponents(inputDirProject);
        settingsPanel.addComponent(btnChooseResultDir);

        layout.addComponent(log);

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
        hl.setVisible(false);

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
                new File(basepath + "/rk7_logo.png"));
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

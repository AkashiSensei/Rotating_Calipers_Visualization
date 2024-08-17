import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * @author : AkashiSensei
 * &#064;date : 2022/12/30 15:53
 */
public class MainFrame extends JFrame {
    private static final int DRAWING_POINT = 0, AUTO_RUNNING = 1, CTRL_RUNNING = 2;
    private final ArrayList<MyPoint> innerPoints = new ArrayList<>();
    private final ArrayList<MyPoint> convexPoints = new ArrayList<>();
    private MyLine maxDis = null;
    private MyLine caliper0 = null;
    private MyLine caliper1 = null;
    private MyLine cross10 = null;
    private MyLine cross11 = null;
    private MyLine cross00 = null;
    private MyLine cross01 = null;
    private int status = DRAWING_POINT;
    private volatile int operate = 0;
    private final JButton buttonAuto = new JButton("自动播放");
    private final JButton buttonNext = new JButton("下一步");
    private final JButton buttonOK = new JButton("点绘制完毕");
    private final JButton buttonReset = new JButton("重置");
    private final JLabel label = new JLabel("单击鼠标以创建点");

    private final Object mutex = new Object();
    public MainFrame() throws HeadlessException {
        this.init();
    }

    public void init() {
        this.setTitle("旋转卡壳 - 平面最远点对");
        this.setSize(1280, 720);
        this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(null);

        initMouseListener();
        initLabel();
        initButton();
    }

    private void initLabel() {
        label.setBounds(500, 300, 280, 120);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("黑体", Font.BOLD, 24));
        label.setForeground(Color.WHITE);

        this.getContentPane().add(label);
    }

    private void initMouseListener() {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(status != DRAWING_POINT) return;
                addInnerPoint(e.getX(), e.getY());
            }
        });
    }

    private void initButton() {
        buttonOK.addActionListener(e -> {
            if(innerPoints.size() < 5) {
                JOptionPane.showMessageDialog(MainFrame.this, "点数量过少！", "警告", JOptionPane.WARNING_MESSAGE);
                return;
            }

            status = CTRL_RUNNING;

            new Thread(this::showRotatingCalipers).start();
            new Thread(() -> {
                while(true){
                    try {
                        if (status == AUTO_RUNNING) {
                            synchronized (mutex) {
                                operate++;
                            }
                        }
                        Thread.sleep(1000);
                    } catch (Exception exception) {
                        System.out.println("Sleep Exception");
                    }
                }
            }).start();
        });

        buttonReset.addActionListener(e -> {
            synchronized (innerPoints) {
                innerPoints.clear();
            }
            label.setVisible(true);
            repaint();

        });



        buttonAuto.addActionListener(e -> {
            if(status == AUTO_RUNNING) {
                status = CTRL_RUNNING;
                buttonAuto.setText("自动播放");
                buttonNext.setEnabled(true);
            }else {
                status = AUTO_RUNNING;
                buttonAuto.setText("手动播放");
                buttonNext.setEnabled(false);
            }
        });

        buttonNext.addActionListener(e -> {
            synchronized (mutex) {
                operate++;
            }
        });

        buttonAuto.setVisible(false);
        buttonNext.setVisible(false);
        buttonNext.setBounds(20, 20, 100, 20);
        buttonAuto.setBounds(130, 20, 100, 20);
        buttonReset.setBounds(130, 20, 100, 20);
        buttonOK.setBounds(20, 20, 100, 20);
        this.getContentPane().add(buttonOK);
        this.getContentPane().add(buttonReset);
        this.getContentPane().add(buttonAuto);
        this.getContentPane().add(buttonNext);
    }

    /**
     * 在控制台输出点集中各点的坐标
     * @param myPointArrayList 点集
     */
    private void printPoints(ArrayList<MyPoint> myPointArrayList) {
        for(MyPoint myPoint : myPointArrayList) {
            System.out.print(myPoint + " ");
        }
        System.out.println();
    }


    /**
     * 鼠标单击后，向点击中插入新的顶点
     * @param x x坐标
     * @param y y坐标
     */
    public void addInnerPoint(int x, int y) {
        label.setVisible(false);
        synchronized (innerPoints){
            MyPoint addingMyPoint = new MyPoint(x, y);
            for (MyPoint myPoint : innerPoints) {
                if (addingMyPoint.equals(myPoint)) {
                    return;
                }
            }
            innerPoints.add(addingMyPoint);
        }
        this.repaint();
    }

    /**
     * 寻找到y坐标最小的点，并将其余点按极角排序，由于坐标系是左手系，故为顺时针顺序
     */
    private void sortAllPoint() {
        MyPoint minMyPoint = new MyPoint(2000, 2000);
        int minInx = -1;

        for(int i = 0; i < innerPoints.size(); i++) {
            if(minMyPoint.compareTo(innerPoints.get(i)) > 0) {
                minMyPoint = innerPoints.get(i);
                minInx = i;
            }
        }

        innerPoints.remove(minInx);
        printPoints(innerPoints);

        MyPoint finalMinMyPoint = minMyPoint;
        innerPoints.sort((o1, o2) -> {
            //System.out.println(o2 + " " + o1 + " " + finalMinMyPoint.cross(o2, o1));
            return finalMinMyPoint.cross(o2, o1);
        });

        innerPoints.add(0, minMyPoint);

        printPoints(innerPoints);
    }

    /**
     * 寻找凸包并高亮
     */
    private void findConvexHull() {
        sortAllPoint();

        int arrayInx = 0;
        int stackTop = -1;

        convexPoints.add(innerPoints.get(arrayInx++));
        stackTop++;
        convexPoints.add(innerPoints.get(arrayInx++));
        stackTop++;

        for(; arrayInx < innerPoints.size(); arrayInx++) {
            while ((convexPoints.get((stackTop - 1)).cross(convexPoints.get(stackTop), innerPoints.get(arrayInx))) < 0) {
                convexPoints.remove(stackTop);
                stackTop--;
            }
            convexPoints.add(innerPoints.get(arrayInx));
            stackTop++;
        }

        for(MyPoint point : convexPoints) {
            innerPoints.remove(point);
        }
    }

    /**
     * 展示旋转卡壳求解平面最远点对的过程
     */
    private void showRotatingCalipers() {
        buttonOK.setVisible(false);
        buttonReset.setVisible(false);


        findConvexHull();

        int n = convexPoints.size();
        convexPoints.add(convexPoints.get(0));

        buttonNext.setVisible(true);
        buttonAuto.setVisible(true);

        repaint();

        double maxDistance = 0;
        int oppoInx = 1;
        waitForNext();
        for(int i = 0; i < n; i++) {
            setCaliperLines(convexPoints.get(i), convexPoints.get(i + 1), convexPoints.get(oppoInx));
            while(setCrossLines(convexPoints.get(i), convexPoints.get(i + 1), convexPoints.get(oppoInx), convexPoints.get(oppoInx + 1)) > 0) {
                oppoInx++;
                oppoInx %= n;

                setCaliperLines(convexPoints.get(i), convexPoints.get(i + 1), convexPoints.get(oppoInx));
                waitForNext();
            }
            if(convexPoints.get(i).dis(convexPoints.get(oppoInx)) > maxDistance) {
                waitForNext();
                maxDistance = convexPoints.get(i).dis(convexPoints.get(oppoInx));
                setMaxDisLine(convexPoints.get(i), convexPoints.get(oppoInx));
            }

            waitForNext();
        }

        if(JOptionPane.showConfirmDialog(this,
                "得到平面最远点对之间欧几里得距离为：" + maxDistance + "\n确定以新建窗口继续，取消以退出应用程序",
                "求解结束", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION) {
            Start.restart();
        }else {
            System.exit(0);
        }
    }

    /**
     * 等待下一次操作
     */
    private void waitForNext() {
        repaint();
        while (operate <= 0) Thread.onSpinWait();
        synchronized (mutex) {
            operate--;
        }
    }

    /**
     * 通过叉积计算两点到给定直线的距离
     * @param lineP0 线段端点之一
     * @param lineP1 线段端点之二
     * @param oppoP0 待比较的顶点之一
     * @param oppoP1 待比较的顶点之二
     * @return 当第二个顶点到直线更远时，返回正值
     */
    private int setCrossLines(MyPoint lineP0, MyPoint lineP1, MyPoint oppoP0, MyPoint oppoP1) {
        cross00 = new MyLine(lineP0, oppoP0);
        cross01 = new MyLine(lineP1, oppoP0);
        cross10 = new MyLine(lineP0, oppoP1);
        cross11 = new MyLine(lineP1, oppoP1);
        return lineP0.cross(lineP1, oppoP1) - lineP0.cross(lineP1, oppoP0);
    }

    /**
     * 设置旋转卡壳的两条直线
     * @param lineP0 直线一过点之一
     * @param lineP1 直线一过点之二
     * @param oppo 直线二过点
     */
    private void setCaliperLines(MyPoint lineP0, MyPoint lineP1, MyPoint oppo) {
        double slope = (double) (lineP1.getY() - lineP0.getY()) / (lineP1.getX() - lineP0.getX());
        caliper0 = new MyLine(0, lineP0.getY() - (int)(slope * (lineP0.getX())), 1280, lineP0.getY() + (int) (slope * (1280 - lineP0.getX())));
        caliper1 = new MyLine(0, oppo.getY() - (int)(slope * (oppo.getX())), 1280, oppo.getY() + (int) (slope * (1280 - oppo.getX())));
    }

    /**
     * 设置当前寻找到的平面最远点对之间的连线
     * @param p0 点对端点之一
     * @param p1 点对端点之二
     */
    private void setMaxDisLine(MyPoint p0, MyPoint p1) {
        maxDis = new MyLine(p0, p1);
    }


    /**
     * 重写绘制方法，用于绘制窗口图像
     * @param g the specified Graphics window
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        paintLine(g);
        paintPoint(g);
    }

    /**
     * 绘制各条直线
     * @param g the specified Graphics window
     */
    private void paintLine(Graphics g) {
        if(caliper0 != null && caliper1 != null) {
            paintSingleLine(g, caliper0, Color.RED);
            paintSingleLine(g, caliper1, Color.RED);
        }

        if(cross00 != null && cross01 != null) {
            paintSingleLine(g, cross00, Color.BLUE);
            paintSingleLine(g, cross01, Color.BLUE);
        }

        if(cross10 != null && cross11 != null) {
            paintSingleLine(g, cross10, Color.GREEN);
            paintSingleLine(g, cross11, Color.GREEN);
        }

        //System.out.println(maxDis);
        if(maxDis != null) {
            paintSingleLine(g, maxDis, Color.WHITE);
        }
    }

    /**
     * 绘制单条直线
     * @param g the specified Graphics window
     * @param line 待绘制的直线
     * @param color 颜色
     */
    private void paintSingleLine(Graphics g, MyLine line, Color color) {
        g.setColor(color);
        g.drawLine(line.getP0().getX(), line.getP0().getY(), line.getP1().getX(), line.getP1().getY());
    }

    /**
     * 绘制各个点
     * @param g the specified Graphics window
     */
    private void paintPoint(Graphics g) {
        for(MyPoint point : innerPoints) {
            if(status == DRAWING_POINT)
                g.setColor(Color.LIGHT_GRAY);
            else
                g.setColor(Color.GRAY);
            g.fillOval(point.getX() - 5, point.getY() - 5, 10, 10);
        }

        for(MyPoint point : convexPoints) {
            g.setColor(Color.ORANGE);
            g.fillOval(point.getX() - 5, point.getY() - 5, 10, 10);
        }
    }
}

/**
 * @author : AkashiSensei
 * &#064;date : 2022/12/31 12:17
 */
public class MyLine {
    private final MyPoint p0;
    private final MyPoint p1;

    public MyLine(MyPoint p0, MyPoint p1) {
        this.p0 = p0;
        this.p1 = p1;
    }
    public MyLine(int x0,int y0,int x1,int y1) {
        this(new MyPoint(x0, y0), new MyPoint(x1, y1));
    }

    public MyPoint getP0() {
        return p0;
    }

    public MyPoint getP1() {
        return p1;
    }

    @Override
    public String toString() {
        return ("[" + p0 + ", " + p1 + "]");
    }
}

/**
 * @author : AkashiSensei
 * &#064;date : 2022/12/30 15:49
 */
public class MyPoint implements Comparable{
    private final int x;
    private final int y;

    public MyPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public MyPoint sub(MyPoint subtract) {
        return new MyPoint(this.x - subtract.x, this.y - subtract.y);
    }

    /**
     * 求解两个以（0， 0）为始点的向量的叉积
     * @param p 第二个向量
     * @return 叉积结果
     */
    public int cross(MyPoint p) {
        return this.x * p.y - this.y * p.x;
    }

    /**
     * 求解两个以this为始点的向量的叉积
     * @param p1 第一个向量的终点
     * @param p2 第二个向量的终点
     * @return 叉积结果
     */
    public int cross(MyPoint p1, MyPoint p2) {
        return (p1.x - this.x) * (p2.y - this.y) - (p1.y - this.y) * (p2.x - this.x);
    }

    /**
     * 求解从this到给定点的欧几里得距离
     * @param p 给定点
     * @return 两点间的欧几里得距离
     */
    public double dis(MyPoint p) {
        return Math.sqrt((p.y - this.y) * (p.y - this.y) + (p.x - this.x) * (p.x - this.x));
    }

    @Override
    public String toString() {
        return ("[" + this.x + ", " + this.y + "]");
    }

    /**
     * 用于寻找y坐标值最小的点，当y坐标值相同时，比较x坐标值
     * @param o the object to be compared.
     * @return 当this的y值或x值（y值相等时）更大时，返回正值
     */
    @Override
    public int compareTo(Object o) {
        //调用方法的y值更大则返回正值
        return (this.y == ((MyPoint) o).y)? this.x - ((MyPoint) o).x : this.y - ((MyPoint) o).y;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MyPoint)) {
            return false;
        }
        if(((MyPoint) obj).x != this.x) {
            return false;
        }
        return ((MyPoint) obj).y == this.y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }



    public static void main(String[] args) {
        MyPoint p0 = new MyPoint(0, 0);
        MyPoint p1 = new MyPoint(1, 0);
        MyPoint p2 = new MyPoint(0,1);

        System.out.println(p1.sub(p0));
        System.out.println(p1.sub(p2));

        System.out.println(p1.cross(p2));
        System.out.println(p2.cross(p1));

        System.out.println(p0.cross(p1, p2));
        System.out.println(p1.cross(p2, p0));
        System.out.println(p1.cross(p0, p2));

        System.out.println(p1.compareTo(p2));
        System.out.println(p1.compareTo(p0));
    }
}

public class NumberTok extends Token {
    public int num;
    public NumberTok(int n) {
        super(256);
        num=n;
    }
    public String toString(){
        return "<"+tag+","+num+">";
    }
}

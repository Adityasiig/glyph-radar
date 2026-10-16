public class Sim {
    static String[] N={"BARS","BLOOM","SCAN","HEART","SPIN","RAIN"};
    static Renderer mk(int i,int s){switch(i){
        case 0:return new BarsRenderer(s);case 1:return new BloomRenderer(s);
        case 2:return new ScanRenderer(s);case 3:return new HeartRenderer(s);
        case 4:return new SpinRenderer(s);default:return new RainRenderer(s);}}
    public static void main(String[] a){
        int S = a.length>0 ? Integer.parseInt(a[0]) : 13;
        float[] ts={0.3f,1.0f,1.8f};
        for(int i=0;i<6;i++){
            System.out.println("\n##### "+N[i]+" #####");
            for(float t:ts){
                int[] px=mk(i,S).render(t);
                System.out.println("t="+t);
                for(int y=0;y<S;y++){StringBuilder sb=new StringBuilder();
                    for(int x=0;x<S;x++){int v=px[y*S+x];
                        sb.append(v>200?"##":v>130?"++":v>60?"::":v>10?"..":"  ");}
                    System.out.println(sb);}
            }
        }
    }
}

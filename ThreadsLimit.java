public class ThreadsLimit extends Thread{
    private static Integer num;
    public static void main(String args[]){
        num = 0;
        for(int i=0;i<1000;i++){ 
            // System.out.println(i);
            ThreadsLimit th = new ThreadsLimit();
            // Thread.sleep(100);
            th.start();
        }
    }

    public void run(){
        num += 1;
        System.out.println("running Thread - "+num);
        System.out.println(num);
    }
}
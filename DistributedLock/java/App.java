import java.util.concurrent.TimeUnit;

import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class App {
    private static RedissonClient redisson;
    public  App(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        redisson = Redisson.create();
    }
    
    public static void main(String[] args) {
        System.out.println("programme started!!");
        App app = new App();
        for(int i=0;i<=10000;i++){
            AcquireLock lock = new AcquireLock(redisson);
            lock.start();
        }
        System.out.println("done!!");
    }
}

class AcquireLock extends Thread{
    private RedissonClient redisson;
    public AcquireLock(RedissonClient redisson){
        this.redisson = redisson;
    }
    public void run(){
        RLock lock = redisson.getFairLock("lock:count");
        // lock.lock(10, TimeUnit.SECONDS);
        try{
            boolean res = lock.tryLock(100, 10, TimeUnit.SECONDS);
            if (res) {
                try {
                    RBucket<String> bucket = redisson.getBucket("count");
                    if(Integer.parseInt(bucket.get())==0){
                        
                        //incrementing value
                        Integer val = Integer.parseInt(bucket.get()) + 1;
                        bucket.set(val.toString());
                        if(Integer.parseInt(bucket.get())==0)
                            System.out.println("Value not incremented");

                        //decrementing value
                        val = Integer.parseInt(bucket.get()) - 1;
                        bucket.set(val.toString());
                        if(Integer.parseInt(bucket.get())!=0)
                            System.out.println("Value not decremented");
                    }
                    else{
                        System.out.println("Error in locking");
                    }
                    System.out.println(bucket.get());
                    Thread.sleep(500);
                } 
                finally {
                    lock.unlock();
                }
            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}

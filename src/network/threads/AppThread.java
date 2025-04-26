package network.threads;

public abstract class AppThread implements Runnable {
    
    protected volatile boolean running;

    public Thread stop() {        
        running = false;
        return Thread.currentThread();
    }
}

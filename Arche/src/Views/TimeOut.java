package Views;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
/**
 * TimeOut class - used for stopping a thread that is taking too long
 * @author Peter Goransson
 *
 */
public class TimeOut {

    Thread interrupter;
    Thread target;
    long timeout;
    boolean success;
    boolean forceStop;

    CyclicBarrier barrier;

    /**
     * 
     * @param target The Runnable target to be executed
     * @param timeout The time in milliseconds before target will be interrupted or stopped
     * @param forceStop If true, will Thread.stop() this target instead of just interrupt() 
     */
    public TimeOut(Runnable target, long timeout, boolean forceStop) {      
        this.timeout = timeout;
        this.forceStop = forceStop;

        this.target = new Thread(target);       
        this.interrupter = new Thread(new Interrupter());

        barrier = new CyclicBarrier(2); // There will always be just 2 threads waiting on this barrier
    }

    public boolean execute() throws InterruptedException {  

        // Start target and interrupter
        target.start();
        interrupter.start();

        // Wait for target to finish or be interrupted by interrupter
        target.join();  

        interrupter.interrupt(); // stop the interrupter    
        try {
            barrier.await(); // Need to wait on this barrier to make sure status is set
        } catch (BrokenBarrierException e) {
            // Something horrible happened, assume we failed
            success = false;
        } 

        return success; // status is set in the Interrupter inner class
    }

    private class Interrupter implements Runnable {

        Interrupter() {}

        @SuppressWarnings("deprecation")
		public void run() {
            try {
                Thread.sleep(timeout); // Wait for timeout period and then kill this target
                if (forceStop) {
                	target.stop(); // Need to use stop instead of interrupt since we're trying to kill this thread
                }
                else {
                    target.interrupt(); // Gracefully interrupt the waiting thread
                }
                success = false;
            } catch (InterruptedException e) {
                success = true;
            }


            try {
                barrier.await(); // Need to wait on this barrier
            } catch (InterruptedException e) {
                // If the Child and Interrupter finish at the exact same millisecond we'll get here
                // In this weird case assume it failed
                success = false;                
            } 
            catch (BrokenBarrierException e) {
                // Something horrible happened, assume we failed
                success = false;
            }

        }

    }
}
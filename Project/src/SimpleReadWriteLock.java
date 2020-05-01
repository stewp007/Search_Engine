import java.util.ConcurrentModificationException;

/**
 * Maintains a pair of associated locks, one for read-only operations and one
 * for writing. The read lock may be held simultaneously by multiple reader
 * threads, so long as there are no writers. The write lock is exclusive, but
 * also tracks which thread holds the lock. If unlock is called by any other
 * thread, a {@link ConcurrentModificationException} is thrown.
 *
 * @see SimpleLock
 * @see SimpleReadWriteLock
 */
public class SimpleReadWriteLock {

    /** The lock used for reading. */
    private final SimpleLock readerLock;

    /** The lock used for writing. */
    private final SimpleLock writerLock;

    /** The number of active readers. */
    private int readers;

    /** The number of active writers; */
    private int writers;

    /**
     * The lock object used for synchronized access of readers and writers. For
     * security reasons, a separate private final lock object is used.
     *
     * @see <a href=
     *      "https://wiki.sei.cmu.edu/confluence/display/java/LCK00-J.+Use+private+final+lock+objects+to+synchronize+classes+that+may+interact+with+untrusted+code">
     *      SEI CERT Oracle Coding Standard for Java</a>
     */
    private Object lock;

    /**
     * Initializes a new simple read/write lock.
     */
    public SimpleReadWriteLock() {
        readerLock = new ReadLock();
        writerLock = new WriteLock();

        lock = new Object();

        readers = 0;
        writers = 0;
    }

    /**
     * Returns the reader lock.
     *
     * @return the reader lock
     */
    public SimpleLock readLock() {
        // NOTE: DO NOT MODIFY THIS METHOD
        return readerLock;
    }

    /**
     * Returns the writer lock.
     *
     * @return the writer lock
     */
    public SimpleLock writeLock() {
        // NOTE: DO NOT MODIFY THIS METHOD
        return writerLock;
    }

    /**
     * Returns the number of active readers.
     * 
     * @return the number of active readers
     */
    public int readers() {
        // NOTE: DO NOT MODIFY THIS METHOD
        synchronized (lock) {
            return readers;
        }
    }

    /**
     * Returns the number of active writers.
     * 
     * @return the number of active writers
     */
    public int writers() {
        // NOTE: DO NOT MODIFY THIS METHOD
        synchronized (lock) {
            return writers;
        }
    }

    /**
     * Determines whether the thread running this code and the other thread are in
     * fact the same thread.
     *
     * @param other the other thread to compare
     * @return true if the thread running this code and the other thread are not
     *         null and have the same ID
     *
     * @see Thread#getId()
     * @see Thread#currentThread()
     */
    public static boolean sameThread(Thread other) {
        // NOTE: DO NOT MODIFY THIS METHOD
        return other != null && other.getId() == Thread.currentThread().getId();
    }

    /**
     * Used to maintain simultaneous read operations.
     */
    private class ReadLock implements SimpleLock {

        /**
         * Will wait until there are no active writers in the system, and then will
         * increase the number of active readers.
         */
        @Override
        public void lock() {
            // NOTE: DO NOT MODIFY THIS METHOD
            synchronized (lock) {
                while (writers > 0) {
                    try {
                        lock.wait();
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }

                assert writers == 0;
                readers++;
            }
        }

        /**
         * Will decrease the number of active readers, and notify any waiting threads if
         * necessary.
         */
        @Override
        public void unlock() {
            synchronized (lock) {
                readers--;
                lock.notifyAll();
            }
        }

    }

    /**
     * Used to maintain exclusive write operations.
     */
    private class WriteLock implements SimpleLock {

        /**
         * The thread used for writing
         */
        private Thread writeThread = null;

        /**
         * Will wait until there are no active readers or writers in the system, and
         * then will increase the number of active writers and update which thread holds
         * the write lock.
         */
        @Override
        public void lock() {
            synchronized (lock) {
                while (readers > 0 || writers > 0) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                writers++;
                writeThread = Thread.currentThread();
            }
        }

        /**
         * Will decrease the number of active writers, and notify any waiting threads if
         * necessary. If unlock is called by a thread that does not hold the lock, then
         * a {@link ConcurrentModificationException} is thrown.
         *
         * @see #sameThread(Thread)
         *
         * @throws ConcurrentModificationException if unlock is called without
         *                                         previously calling lock or if unlock
         *                                         is called by a thread that does not
         *                                         hold the write lock
         */
        @Override
        public void unlock() throws ConcurrentModificationException {
            synchronized (lock) {
                if (writeThread == null || !sameThread(writeThread) || writers == 0) {
                    throw new ConcurrentModificationException();
                }
                writers--;
                lock.notifyAll();
            }
        }
    }
}

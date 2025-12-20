class Consumer extends Thread {
    private final BoundedBuffer buffer;

    public Consumer(BoundedBuffer buffer, String name) {
        super(name);
        this.buffer = buffer;
    }

    public void run() {
        try {
            for (int i = 1; i <= 10; i++) {
                buffer.consume();
                Thread.sleep(500); // simulate consumption time
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

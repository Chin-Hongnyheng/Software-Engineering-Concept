class Producer extends Thread {
    private final BoundedBuffer buffer;

    public Producer(BoundedBuffer buffer, String name) {
        super(name);
        this.buffer = buffer;
    }

    public void run() {
        try {
            for (int i = 1; i <= 10; i++) {
                buffer.produce(i);
                // simulate production time
                Thread.sleep(300);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

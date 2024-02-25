package backageServer;

import backageServer.connect.ConnectSocket;

public final class Server {

    private static class Completion {
        private int running;
        private boolean fatalError;

        Completion(int running) {
            this.running = running;
        }

        synchronized void addCompleted(boolean fatalError) {
            --running;
            if (fatalError) {
                this.fatalError = true;
            }
            if (running == 0 || this.fatalError) {
                notify();
            }
        }

        synchronized void await() {
            try {
                while (running > 0 && !fatalError) {
                    wait();
                }
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    public static void main(String... args) {
        Completion completion = new Completion(1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                ConnectSocket.start();
                completion.addCompleted(false);
            }
        }).start();

        completion.await();
    }
}

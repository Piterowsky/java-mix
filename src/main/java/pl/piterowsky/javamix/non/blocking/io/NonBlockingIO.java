package pl.piterowsky.javamix.non.blocking.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class NonBlockingIO {

    private final Map<SocketChannel, Queue<ByteBuffer>> pendingData = new HashMap<>();
    private ServerSocketChannel ssc;
    private Selector selector;

    public void start() throws IOException {
        selector = Selector.open();
        ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.register(selector, SelectionKey.OP_READ);

        while (true) {
            selector.select();
            var keys = selector.selectedKeys();
            for (Iterator<SelectionKey> it = keys.iterator(); it.hasNext();) {
                var sk = it.next();
                it.remove();

                if (sk.isValid()) {
                    if (sk.isAcceptable()) {
                        handleAccept();
                    } else if (sk.isReadable()) {
                        handleRead(sk);
                    } else if (sk.isWritable()) {
                        handleWrite(sk);
                    }
                }
            }
        }
    }

    private void handleAccept() throws IOException {
        SocketChannel sc = ssc.accept();

        if (sc != null) {
            sc.configureBlocking(false);
            sc.register(selector, SelectionKey.OP_READ);

            pendingData.put(sc, new LinkedList<>());
        }
    }

    private void handleRead(SelectionKey sk) throws IOException {
        SocketChannel sc = (SocketChannel) sk.channel();
        ByteBuffer bb = ByteBuffer.allocate(120);

        int read = sc.read(bb);
        if (read == -1) {
            pendingData.remove(sc);
            sc.close();
        } else if (read > 0) {
            bb.flip();
            for (int i = 0; i < bb.limit(); i++) {
                bb.put(i, (byte) Character.toUpperCase((char) bb.get(i)));
            }
            pendingData.get(sc).add(bb);
            sk.interestOps(SelectionKey.OP_WRITE);
        }
    }

    private void handleWrite(SelectionKey sk) throws IOException {
        SocketChannel sc = (SocketChannel) sk.channel();
        Queue<ByteBuffer> queue = pendingData.get(sc);

        while (!queue.isEmpty()) {
            ByteBuffer bb = queue.peek();
            int write = sc.write(bb);
            if (write == -1) {
                pendingData.remove(sc);
                sc.close();
                return;
            } else if (bb.hasRemaining()) {
                return;
            }

            queue.remove();
        }

        sk.interestOps(SelectionKey.OP_READ);
    }

}

package pl.coffeepower.blog.messagebus.raw;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import pl.coffeepower.blog.messagebus.Configuration;
import pl.coffeepower.blog.messagebus.Subscriber;
import pl.coffeepower.blog.messagebus.util.BytesEventFactory;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

@Log4j2
final class RawSubscriber implements Subscriber {

    private final AtomicBoolean opened = new AtomicBoolean(false);
    private final Disruptor<BytesEventFactory.BytesEvent> disruptor;
    private final RingBuffer<BytesEventFactory.BytesEvent> ringBuffer;
    //    private final MulticastSocket multicastSocket;
//    private final InetAddress multicastAddr;
//    private final NetworkInterface networkInterface;
    @Inject
    private Handler handler;

    @Inject
    private RawSubscriber(@NonNull Configuration configuration, @NonNull Disruptor<BytesEventFactory.BytesEvent> disruptor) {
        throw new IllegalAccessError();
//        this.disruptor = disruptor;
//        this.disruptor.handleEventsWith((event, sequence, endOfBatch) -> {
//            Preconditions.checkNotNull(handler);
//            handler.received(event.getBuffer(), event.getCurrentLength());
//        });
//        this.ringBuffer = this.disruptor.start();
//        this.multicastAddr = InetAddress.getByName(configuration.getMulticastAddress());
//        this.networkInterface = NetworkInterface.getByInetAddress(InetAddress.getByName(configuration.getMulticastAddress()));
//        this.multicastSocket = new MulticastSocket(configuration.getMulticastPort());
//        this.multicastSocket.joinGroup(new ServerSocket(configuration.getMulticastPort()), networkInterface);
//        opened.set(true);
    }

    @Override
    public void register(@NonNull Handler handler) {
        this.handler = handler;
    }

    @Override
    public boolean isOpened() {
        return opened.get();
    }

    @Override
    public void close() throws Exception {
//        if (this.multicastSocket != null) {
//            this.multicastSocket.close();
//        }
        this.handler = null;
        opened.set(false);
    }
}

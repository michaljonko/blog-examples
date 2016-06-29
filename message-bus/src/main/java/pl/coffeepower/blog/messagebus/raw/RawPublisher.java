package pl.coffeepower.blog.messagebus.raw;

import lombok.extern.log4j.Log4j2;

import pl.coffeepower.blog.messagebus.Publisher;

@Log4j2
final class RawPublisher implements Publisher {

    private RawPublisher() {
        throw new IllegalAccessError();
    }

    @Override
    public boolean send(byte[] data) {
        return false;
    }

    @Override
    public boolean isOpened() {
        return false;
    }

    @Override
    public void close() throws Exception {

    }
}

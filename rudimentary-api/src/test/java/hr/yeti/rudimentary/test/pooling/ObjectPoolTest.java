package hr.yeti.rudimentary.test.pooling;

import hr.yeti.rudimentary.pooling.ObjectPoolException;
import hr.yeti.rudimentary.pooling.ObjectPoolSettings;
import hr.yeti.rudimentary.pooling.spi.ObjectPool;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class ObjectPoolTest {

    final ObjectPoolSettings objectPoolSettings = new ObjectPoolSettings(3, 10, 10, 0);

    @Test
    public void test_pool_shutdown() {
        IntegerPool pool;

        given:  pool = new IntegerPool();
        pool.initialize();

        then:   assertEquals(ObjectPool.PoolStatus.UP, pool.status());

        and:    when:   pool.destroy();

        then:   assertEquals(ObjectPool.PoolStatus.DOWN, pool.status());
    }

    @Test
    public void test_pool_object_count() {
        IntegerPool pool;

        given:  pool = new IntegerPool();
        pool.initialize();

        when:   pool.borrow();

        then:   assertEquals(2, pool.objectCount());

        pool.destroy();
    }

    @Test
    public void test_pool_initial_settings() {
        IntegerPool pool;

        given:  pool = new IntegerPool();
        pool.initialize();

        expect: assertEquals(3, pool.objectCount());

        pool.destroy();
    }

    @Test
    public void test_pool_borrow_object_in_FIFO_order() {
        IntegerPool pool;

        given:  pool = new IntegerPool();
        pool.initialize();

        Object intObject0;
        Object intObject1;
        Object intObject2;

        when:   intObject0 = pool.borrow();
        intObject1 = pool.borrow();
        intObject2 = pool.borrow();

        then:   assertEquals(0, intObject0);
        assertEquals(1, intObject1);
        assertEquals(2, intObject2);

        and:    when: // Return objects to pool in reverse order
                        pool.release(intObject2);
        pool.release(intObject1);
        pool.release(intObject0);
        intObject0 = pool.borrow();

        then: // Expecting first object returned to pool
                assertEquals(2, intObject0);

        pool.destroy();
    }

    @Test
    public void test_pool_borrow_and_return_object() {
        IntegerPool pool;

        given:  pool = new IntegerPool();
        pool.initialize();

        Object intObject;

        when:   intObject = pool.borrow();
        pool.release(intObject);

        then:   assertEquals(3, pool.objectCount());

        pool.destroy();
    }

    @Test
    public void test_pool_borrow_object_on_empty_pool() {
        IntegerPool pool;

        given:  pool = new IntegerPool();
        pool.initialize();

        Object intObject;

        when:   pool.borrow();
        pool.borrow();
        pool.borrow();

        and:    when: // Try to borrow when pool is empty
                        intObject = pool.borrow();

        then: // Expect brand new object to be created
                assertEquals(3, intObject);

        pool.destroy();
    }

    public class IntegerPool extends ObjectPool {

        private final AtomicInteger value = new AtomicInteger(0);

        @Override
        protected Object createObject() throws ObjectPoolException {
            return value.getAndIncrement();
        }

        @Override
        protected ObjectPoolSettings settings() {
            return objectPoolSettings;
        }

    }

}

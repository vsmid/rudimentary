package hr.yeti.rudimentary.test.exception;

import hr.yeti.rudimentary.exception.ExceptionInfo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class TestExceptionInfo {

    @Test
    public void test_defaultExceptionInfo_method_generation_values() {
        // setup:
        ExceptionInfo exceptionInfo;

        when:
        exceptionInfo = ExceptionInfo.defaultExceptionInfo();

        then:
        assertNotNull(exceptionInfo);
        assertEquals(500, exceptionInfo.getHttpStatus());
        assertEquals("Internal Server Error.", exceptionInfo.getContent());
        assertFalse(exceptionInfo.isOverride());
    }

    @Test
    public void test_constructor_created_values() {
        // setup:
        ExceptionInfo exceptionInfo;

        when:
        exceptionInfo = new ExceptionInfo(200, "ok".getBytes());

        then:
        assertNotNull(exceptionInfo);
        assertEquals(200, exceptionInfo.getHttpStatus());
        assertEquals("ok", exceptionInfo.getContent());
        assertTrue(exceptionInfo.isOverride());

        and:
        when:
        exceptionInfo = new ExceptionInfo(200, "ok".getBytes());

        then:
        assertNotNull(exceptionInfo);
        assertEquals(200, exceptionInfo.getHttpStatus());
        assertEquals("ok", exceptionInfo.getContent());
        assertTrue(exceptionInfo.isOverride());
    }

}

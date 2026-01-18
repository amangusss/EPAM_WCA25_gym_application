package com.github.amangusss.gym_application.bdd.context;

import io.cucumber.spring.ScenarioScope;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashMap;
import java.util.Map;

@Component
@ScenarioScope
public class SharedTestContext {

    private final Map<String, Object> context = new HashMap<>();

    @Getter
    @Setter
    private ResultActions resultActions;

    @Getter
    @Setter
    private Exception lastException;

    @Getter
    @Setter
    private String jwtToken;

    @Getter
    @Setter
    private String currentUsername;

    public void set(String key, Object value) {
        context.put(key, value);
    }

    public Object get(String key) {
        return context.get(key);
    }

    public <T> T get(String key, Class<T> type) {
        return type.cast(context.get(key));
    }

    public void reset() {
        context.clear();
        resultActions = null;
        lastException = null;
        jwtToken = null;
        currentUsername = null;
    }
}
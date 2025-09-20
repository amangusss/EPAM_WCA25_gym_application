package com.github.amangusss.gym_application.storage;

import com.github.amangusss.gym_application.entity.Training;
import org.springframework.stereotype.Component;

@Component("trainingStorage")
public class TrainingStorage extends InMemoryStorage<Training>{

    @Override
    protected Long extractId(Training entity) {
        return entity.getId();
    }

    @Override
    protected void setId(Training entity, Long id) {
        entity.setId(id);
    }
}

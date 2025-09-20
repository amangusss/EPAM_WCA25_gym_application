package com.github.amangusss.gym_application.storage;

import com.github.amangusss.gym_application.entity.Trainee;
import org.springframework.stereotype.Component;

@Component("traineeStorage")
public class TraineeStorage extends InMemoryStorage<Trainee>{

    @Override
    protected Long extractId(Trainee entity) {
        return entity.getId();
    }

    @Override
    protected void setId(Trainee entity, Long id) {
        entity.setId(id);
    }
}

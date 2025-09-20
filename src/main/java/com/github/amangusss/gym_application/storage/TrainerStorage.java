package com.github.amangusss.gym_application.storage;

import com.github.amangusss.gym_application.entity.Trainer;
import org.springframework.stereotype.Component;

@Component("trainerStorage")
public class TrainerStorage extends InMemoryStorage<Trainer>{

    @Override
    protected Long extractId(Trainer entity) {
        return entity.getId();
    }

    @Override
    protected void setId(Trainer entity, Long id) {
        entity.setId(id);
    }
}

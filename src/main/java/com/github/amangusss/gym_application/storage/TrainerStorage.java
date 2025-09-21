package com.github.amangusss.gym_application.storage;

import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.util.constants.ConfigConstants;
import org.springframework.stereotype.Component;

@Component(ConfigConstants.BEAN_TRAINER_STORAGE)
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

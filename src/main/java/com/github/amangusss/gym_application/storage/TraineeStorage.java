package com.github.amangusss.gym_application.storage;

import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.util.constants.ConfigConstants;
import org.springframework.stereotype.Component;

@Component(ConfigConstants.BEAN_TRAINEE_STORAGE)
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

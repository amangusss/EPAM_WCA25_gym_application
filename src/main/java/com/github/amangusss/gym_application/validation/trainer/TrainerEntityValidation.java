package com.github.amangusss.gym_application.validation.trainer;

import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.validation.CommonValidation;

public interface TrainerEntityValidation extends CommonValidation {

    void validateTrainerForCreationOrUpdate(Trainer trainer);
}

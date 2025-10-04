package com.github.amangusss.gym_application.util.validation.service.trainer;

import com.github.amangusss.gym_application.entity.trainer.Trainer;
import com.github.amangusss.gym_application.util.validation.service.CommonValidation;

public interface TrainerServiceValidation extends CommonValidation {

    void validateTrainerForCreationOrUpdate(Trainer trainer);
}
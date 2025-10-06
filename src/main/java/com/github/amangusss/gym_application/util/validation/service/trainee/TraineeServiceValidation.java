package com.github.amangusss.gym_application.util.validation.service.trainee;

import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.util.validation.service.CommonValidation;

public interface TraineeServiceValidation extends CommonValidation {

    void validateTraineeForCreationOrUpdate(Trainee trainee);
}
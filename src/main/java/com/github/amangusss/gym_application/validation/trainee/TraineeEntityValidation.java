package com.github.amangusss.gym_application.validation.trainee;

import com.github.amangusss.gym_application.entity.trainee.Trainee;
import com.github.amangusss.gym_application.validation.CommonValidation;

public interface TraineeEntityValidation extends CommonValidation {

    void validateTraineeForCreationOrUpdate(Trainee trainee);
}
package com.hiperium.city.tasks.api;

import com.hiperium.city.tasks.api.common.AbstractContainerBase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled
@SpringBootTest
class TasksApplicationTests extends AbstractContainerBase {

	@Test
	void contextLoads() {
		Assertions.assertThat(POSTGRES_CONTAINER).isNotNull();
		Assertions.assertThat(DYNAMODB_CONTAINER).isNotNull();
	}

}

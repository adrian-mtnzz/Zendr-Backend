package com.zendr.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;


@DataMongoTest
@ActiveProfiles("dev")
class BackendApplicationTests {


	@Test
	void contextLoads() {}

}

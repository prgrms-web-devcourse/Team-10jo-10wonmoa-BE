package com.prgrms.tenwonmoa.common;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.tenwonmoa.common.annotation.RestDocsTest;

@RestDocsTest
public class BaseControllerUnitTest {
	@Autowired
	protected ObjectMapper objectMapper;
	@Autowired
	protected RestDocumentationResultHandler restDocs;
	protected MockMvc mockMvc;

	@BeforeEach
	void setUp(WebApplicationContext context, RestDocumentationContextProvider provider) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
			.apply(MockMvcRestDocumentation.documentationConfiguration(provider))
			.apply(springSecurity())
			.alwaysDo(restDocs)
			.addFilters(new CharacterEncodingFilter("UTF-8", true)) // 한글 깨짐 방지
			.build();
	}
}

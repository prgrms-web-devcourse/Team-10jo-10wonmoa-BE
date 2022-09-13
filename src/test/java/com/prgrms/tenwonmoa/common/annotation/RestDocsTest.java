package com.prgrms.tenwonmoa.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.RestDocumentationExtension;

import com.prgrms.tenwonmoa.common.RestDocsConfig;
import com.prgrms.tenwonmoa.config.JwtConfigure;
import com.prgrms.tenwonmoa.config.WebSecurityConfig;
import com.prgrms.tenwonmoa.domain.user.security.jwt.filter.JwtAuthenticationFilter;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@WebMvcTest(
	excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfig.class),
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtConfigure.class),
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
	}
)
@ExtendWith(RestDocumentationExtension.class)
@MockBean(JpaMetamodelMappingContext.class)
@Import(RestDocsConfig.class)
public @interface RestDocsTest {
}

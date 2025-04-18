package simple.simple_auth.config.observability;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Slf4j
@Component
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request,
																	HttpServletResponse response,
																	FilterChain filterChain)
					throws ServletException, IOException {

		CachedBodyRequestWrapper requestWrapper = new CachedBodyRequestWrapper(request);
		ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

		String requestBody = getRequestBody(requestWrapper);
		log.info("┌─── START {} {} - Body: {}",
						request.getMethod(),
						request.getRequestURI(),
						requestBody.isEmpty() ? "[empty]" : requestBody);

		try {
			filterChain.doFilter(requestWrapper, responseWrapper);
		} finally {
			String responseBody = getResponseBody(responseWrapper);
			log.info("└─── END {} {} - Status: {} - Body: {}",
							request.getMethod(),
							request.getRequestURI(),
							responseWrapper.getStatus(),
							responseBody.isEmpty() ? "[empty]" : responseBody);

			responseWrapper.copyBodyToResponse();
		}
	}

	private static class CachedBodyRequestWrapper extends HttpServletRequestWrapper {
		private final byte[] cachedBody;

		public CachedBodyRequestWrapper(HttpServletRequest request) throws IOException {
			super(request);
			this.cachedBody = cacheBody(request);
		}

		private byte[] cacheBody(HttpServletRequest request) throws IOException {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			request.getInputStream().transferTo(baos);
			return baos.toByteArray();
		}

		@Override
		public ServletInputStream getInputStream() {
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.cachedBody);
			return new ServletInputStream() {
				@Override
				public int read() {
					return byteArrayInputStream.read();
				}

				@Override
				public boolean isFinished() {
					return byteArrayInputStream.available() == 0;
				}

				@Override
				public boolean isReady() {
					return true;
				}

				@Override
				public void setReadListener(ReadListener readListener) {
				}
			};
		}
	}

	private String getRequestBody(CachedBodyRequestWrapper wrapper) {
		try {
			return new String(wrapper.cachedBody, StandardCharsets.UTF_8);
		} catch (Exception e) {
			return "[error reading body]";
		}
	}

	private String getResponseBody(ContentCachingResponseWrapper wrapper) {
		try {
			return new String(wrapper.getContentAsByteArray(), wrapper.getCharacterEncoding());
		} catch (Exception e) {
			return "[error reading response]";
		}
	}
}
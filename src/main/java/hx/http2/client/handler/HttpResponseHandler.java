package hx.http2.client.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author mingliang
 * @Date 2018-07-27 14:24
 */
public class HttpResponseHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpResponseHandler.class);

//    public <T> T execute(final HttpHost target, final HttpRequest request,
//                         final ResponseHandler<? extends T> responseHandler, final HttpContext context)
//            throws IOException, ClientProtocolException {
//
//        Args.notNull(responseHandler, "Response handler");
//
//        final HttpResponse response = execute(target, request, context);
//
//        final T result;
//        try {
//            result = responseHandler.handleResponse(response);
//        } catch (final Exception t) {
//            final HttpEntity entity = response.getEntity();
//            try {
//                EntityUtils.consume(entity);
//            } catch (final Exception t2) {
//                // Log this exception. The original exception is more
//                // important and will be thrown to the caller.
//                LOGGER.warn("Error consuming content after an exception.", t2);
//            }
//            if (t instanceof RuntimeException) {
//                throw (RuntimeException) t;
//            }
//            if (t instanceof IOException) {
//                throw (IOException) t;
//            }
//            throw new UndeclaredThrowableException(t);
//        }
//
//        // Handling the response was successful. Ensure that the content has
//        // been fully consumed.
//        final HttpEntity entity = response.getEntity();
//        EntityUtils.consume(entity);//看这里看这里
//        return result;
//    }
}

package software.amazon.noggin.runtime.lambda;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import org.junit.Test;

public class LambdaApplicationTest {
    private String stringInput = "{ \"resource\": \"/\", \"path\": \"/\", \"httpMethod\": \"GET\", \"headers\": { \"Accept\": \"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\", \"Accept-Encoding\": \"gzip, deflate, br\", \"Accept-Language\": \"en-US,en;q=0.5\", \"cache-control\": \"max-age=0\", \"CloudFront-Forwarded-Proto\": \"https\", \"CloudFront-Is-Desktop-Viewer\": \"true\", \"CloudFront-Is-Mobile-Viewer\": \"false\", \"CloudFront-Is-SmartTV-Viewer\": \"false\", \"CloudFront-Is-Tablet-Viewer\": \"false\", \"CloudFront-Viewer-Country\": \"US\", \"Host\": \"tdjdkpcg6d.execute-api.us-west-2.amazonaws.com\", \"upgrade-insecure-requests\": \"1\", \"User-Agent\": \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:60.0) Gecko/20100101 Firefox/60.0\", \"Via\": \"2.0 030fe0607711293dda988e571617a9f3.cloudfront.net (CloudFront)\", \"X-Amz-Cf-Id\": \"NDLy-b1qzzv9McLHgdjzCPrKuN5IAvhnsCvq72Djhl6haeq-KT4dsg==\", \"X-Amzn-Trace-Id\": \"Root=1-5c3e463d-7780fbbc29d948d0367345c0\", \"X-Forwarded-For\": \"54.240.196.191, 70.132.31.76\", \"X-Forwarded-Port\": \"443\", \"X-Forwarded-Proto\": \"https\" }, \"multiValueHeaders\": { \"Accept\": [ \"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\" ], \"Accept-Encoding\": [ \"gzip, deflate, br\" ], \"Accept-Language\": [ \"en-US,en;q=0.5\" ], \"cache-control\": [ \"max-age=0\" ], \"CloudFront-Forwarded-Proto\": [ \"https\" ], \"CloudFront-Is-Desktop-Viewer\": [ \"true\" ], \"CloudFront-Is-Mobile-Viewer\": [ \"false\" ], \"CloudFront-Is-SmartTV-Viewer\": [ \"false\" ], \"CloudFront-Is-Tablet-Viewer\": [ \"false\" ], \"CloudFront-Viewer-Country\": [ \"US\" ], \"Host\": [ \"tdjdkpcg6d.execute-api.us-west-2.amazonaws.com\" ], \"upgrade-insecure-requests\": [ \"1\" ], \"User-Agent\": [ \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:60.0) Gecko/20100101 Firefox/60.0\" ], \"Via\": [ \"2.0 030fe0607711293dda988e571617a9f3.cloudfront.net (CloudFront)\" ], \"X-Amz-Cf-Id\": [ \"NDLy-b1qzzv9McLHgdjzCPrKuN5IAvhnsCvq72Djhl6haeq-KT4dsg==\" ], \"X-Amzn-Trace-Id\": [ \"Root=1-5c3e463d-7780fbbc29d948d0367345c0\" ], \"X-Forwarded-For\": [ \"54.240.196.191, 70.132.31.76\" ], \"X-Forwarded-Port\": [ \"443\" ], \"X-Forwarded-Proto\": [ \"https\" ] }, \"queryStringParameters\": null, \"multiValueQueryStringParameters\": null, \"pathParameters\": null, \"stageVariables\": null, \"requestContext\": { \"resourceId\": \"jsmsk0f26m\", \"resourcePath\": \"/\", \"httpMethod\": \"GET\", \"extendedRequestId\": \"Tj_plEwUvHcFQ0A=\", \"requestTime\": \"15/Jan/2019:20:44:45 +0000\", \"path\": \"/prod\", \"accountId\": \"131990247566\", \"protocol\": \"HTTP/1.1\", \"stage\": \"prod\", \"domainPrefix\": \"tdjdkpcg6d\", \"requestTimeEpoch\": 1547585085358, \"requestId\": \"643abd76-1906-11e9-8391-1f275a451f24\", \"identity\": { \"cognitoIdentityPoolId\": null, \"accountId\": null, \"cognitoIdentityId\": null, \"caller\": null, \"sourceIp\": \"54.240.196.191\", \"accessKey\": null, \"cognitoAuthenticationType\": null, \"cognitoAuthenticationProvider\": null, \"userArn\": null, \"userAgent\": \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:60.0) Gecko/20100101 Firefox/60.0\", \"user\": null }, \"domainName\": \"tdjdkpcg6d.execute-api.us-west-2.amazonaws.com\", \"apiId\": \"tdjdkpcg6d\" }, \"body\": null, \"isBase64Encoded\": false }";

    @Test
    public void test() throws Exception {
//        InputStream input = new ByteArrayInputStream(stringInput.getBytes(UTF_8));
//        OutputStream output = new ByteArrayOutputStream();
//        new LambdaApplication().handle(input, output);
    }
}

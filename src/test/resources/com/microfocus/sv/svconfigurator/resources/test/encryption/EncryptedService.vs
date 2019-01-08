<?xml version="1.0" encoding="utf-8"?>
<vs:virtualService version="3.50.9.99" id="2b3e6023-d0e7-49dd-b6bf-24ee9f39e2d4" name="Enc2 Service" description="Virtual service using REST" activeConfiguration="fb8e9d42-d4ce-4807-87cf-4f901ba981d2" nonExistentRealService="false" xmlns:vs="http://hp.com/SOAQ/ServiceVirtualization/2010/">
  <EncryptionMetadata>
    <EncryptedNode xpointer="xmlns(ns0=http://hp.com/SOAQ/ServiceVirtualization/2010/)xpath(//ns0:userNamePassword/@enc-password)" targetName="password" />
    <EncryptedNode xpointer="xmlns(ns0=http://hp.com/SOAQ/ServiceVirtualization/2010/)xpath(//ns0:x509Certificate/@enc-certificateData)" targetName="certificateData" />
  </EncryptionMetadata>
  <vs:projectId ref="{366ABE23-60CD-4DD7-808E-8AEA19EA1DB8}" />
  <vs:projectName>EncryptedServiceVirtualizationProject</vs:projectName>
  <vs:serviceDescription ref="6ffc6003-b772-41f1-b6d8-f16c9d964425" />
  <vs:virtualEndpoint type="HTTPProxy" address="enc2" realAddress="http://enc2/" isTemporary="false" isDiscovered="false" id="52813e09-3a07-4852-a044-2c67b2fbb81f" name=" Endpoint">
    <vs:agent ref="HttpProxyAgent" />
  </vs:virtualEndpoint>
  <vs:dataModel ref="b2259412-69f9-42fa-abba-f2ba69ad876f" />
  <vs:performanceModel ref="0e93550d-df46-4cbf-9de3-d9f7b19c2aaf" />
  <vs:performanceModel ref="107e1bfa-2e98-4047-8bc9-3d4140336144" />
  <vs:configuration id="fb8e9d42-d4ce-4807-87cf-4f901ba981d2" name="Enc2 Service Configuration">
    <vs:httpAuthentication>None</vs:httpAuthentication>
    <vs:httpAuthenticationAutodetect>True</vs:httpAuthenticationAutodetect>
    <vs:credentialStore id="46d68037-162e-475a-8307-86b405101852">
      <vs:credentials>
        <vs:userNamePassword credentialName="UsernamePasswordCredential 1" userName="una" enc-password="kbHtDCLCUEX/08XSZXFL72LTi1HVmSR4tKQcP7Zh8fs=" />
        <vs:userNamePassword credentialName="UsernamePasswordCredential 2" userName="unb" enc-password="E0r2T9VZrQZOrAzUSq/EG4NDzU8F9tPntCX8jhv8Qi8=" />
      </vs:credentials>
      <vs:identities>
        <vs:identity identityId="una">
          <vs:linkedCredential logicalId="UsernamePassword" credentialName="UsernamePasswordCredential 1" />
        </vs:identity>
        <vs:identity identityId="unb">
          <vs:linkedCredential logicalId="UsernamePassword" credentialName="UsernamePasswordCredential 2" />
        </vs:identity>
      </vs:identities>
    </vs:credentialStore>
    <vs:securityConfiguration>
      <security />
      <clientSecurity />
      <serviceSecurity />
      <credentials>
        <clientCertificate value="Identity[0].Certificate" />
        <serviceCertificate value="ServiceIdentity.Certificate" />
        <userName value="Identity[0].UsernamePassword" />
      </credentials>
    </vs:securityConfiguration>
    <vs:messageSchemaLocked>False</vs:messageSchemaLocked>
    <vs:logMessages>False</vs:logMessages>
  </vs:configuration>
</vs:virtualService>
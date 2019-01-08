<?xml version="1.0" encoding="utf-8"?>
<vs:virtualService version="3.50.1718.37796" id="67242b01-3ed5-4127-aab1-639c953ca86c" name="Member Accounts" description="Virtual service created from service 'MemberAccounts' located at http://localhost:8101/ServiceSimulation/Demo/MemberAccountsService/MemberAccountsService" activeConfiguration="c26d692c-396a-4362-ac92-a1ddf8f26adb" nonExistentRealService="false" xmlns:vs="http://hp.com/SOAQ/ServiceVirtualization/2010/">
  <vs:projectId ref="{9B3A0C94-3965-4342-90F4-995D626F5A39}" />
  <vs:projectName>Simulation</vs:projectName>
  <vs:serviceDescription ref="7c4c2b60-e3e3-48d2-8f5b-2bea99dd3ce7" />
  <vs:virtualEndpoint type="HTTP" address="MemberAccounts" realAddress="http://localhost:8101/ServiceSimulation/Demo/MemberAccountsService/MemberAccountsService" isTemporary="false" isDiscovered="false" id="cddca2be-014c-4129-bbbd-116dff1d2d82" name="Member Accounts Endpoint">
    <vs:agent ref="HttpAgent" />
  </vs:virtualEndpoint>
  <vs:dataModel ref="7e84f205-806f-4fa5-9ce6-11b8ac44cfa1" />
  <vs:performanceModel ref="e80b8432-b21e-4398-b724-dcbcd35f94e5" />
  <vs:performanceModel ref="710262de-93b1-45d2-9d48-fee401e739b1" />
  <vs:configuration id="c26d692c-396a-4362-ac92-a1ddf8f26adb" name="Member Accounts Configuration">
    <vs:httpAuthentication>None</vs:httpAuthentication>
    <vs:httpAuthenticationAutodetect>True</vs:httpAuthenticationAutodetect>
    <vs:credentialStore id="7577b76e-6998-4063-b7a3-c2276abe4f9f">
      <vs:credentials>
        <vs:userNamePassword credentialName="UsernamePasswordCredential 1" userName="hpguest" password="hpguest" />
      </vs:credentials>
      <vs:identities>
        <vs:identity identityId="hpguest">
          <vs:linkedCredential logicalId="UsernamePassword" credentialName="UsernamePasswordCredential 1" />
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
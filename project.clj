(defproject betfair-lib "0.1.0-SNAPSHOT"
  :description "A clojure library for accessing the betfair API"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main betfair-lib.core
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [clj-time "0.6.0"]
                 [org.clojure/data.json "0.1.3"]
                 [org.apache.ws.commons.schema/XmlSchema "1.4.3"]
                 [javax.activation/activation "1.1"]
                 [org.apache.ws.commons.axiom/axiom-api "1.2.8"]
                 [org.apache.ws.commons.axiom/axiom-dom "1.2.8"]
                 [org.apache.ws.commons.axiom/axiom-impl "1.2.8"]
                 [org.apache.axis2/axis2-adb "1.5.1"]
                 [org.apache.axis2/axis2-ant-plugin "1.5.1"]
                 [org.apache.axis2/axis2-clustering "1.5.1"]
                 [org.apache.axis2/axis2-codegen "1.5.1"]
                 [org.apache.axis2/axis2-corba "1.5.1"]
                 [org.apache.axis2/axis2-fastinfoset "1.5.1"]
                 [org.apache.axis2/axis2-java2wsdl "1.5.1"]
                 [org.apache.axis2/axis2-java2wsdl "1.5.1"]
                 [org.apache.axis2/axis2-jaxbri "1.5.1"]
                 [org.apache.axis2/axis2-jaxws "1.5.1"]
                 [org.apache.axis2/axis2-jibx "1.5.1"]
                 [org.apache.axis2/axis2-json "1.5.1"]
                 [org.apache.axis2/axis2-kernel "1.5.1"]
                 [org.apache.axis2/axis2-metadata "1.5.1"]
                 [org.apache.axis2/axis2-mtompolicy "1.5.1"]
                 [org.apache.axis2/axis2-saaj "1.5.1"]
                 [org.apache.axis2/axis2-spring "1.5.1"]
                 [org.apache.axis2/axis2-transport-http "1.5.1"]
                 [org.apache.axis2/axis2-transport-local "1.5.1"]
                 [bcel/bcel "5.1"]
                 [commons-codec/commons-codec "1.3"]
                 [commons-fileupload/commons-fileupload "1.2"]
                 [commons-httpclient/commons-httpclient "3.1"]
                 [commons-io/commons-io "1.4"]
                 [commons-lang/commons-lang "2.3"]
                 [commons-logging/commons-logging "1.1.1"]
                 [org.apache.geronimo.specs/geronimo-annotation_1.0_spec "1.1"]
                 [org.apache.geronimo.specs/geronimo-jaxws_2.1_spec "1.0"]
                 [org.apache.geronimo.specs/geronimo-saaj_1.3_spec "1.0.1"]
                 [org.apache.geronimo.specs/geronimo-stax-api_1.0_spec "1.0.1"]
                 [org.apache.geronimo.specs/geronimo-ws-metadata_2.0_spec "1.1.2"]
                 [org.apache.httpcomponents/httpcore "4.0"]
                 [jalopy/jalopy "1.5rc3"]
                 [javax.xml/jaxb-api "2.1"]
                 [com.sun.xml.bind/jaxb-impl "2.1.7"]
                 [com.sun.xml.bind/jaxb-xjc "2.1.7"]
                 [jaxen/jaxen "1.1.1"]
                 [org.codehaus.jettison/jettison "1.0-RC2"]
                 [org.jibx/jibx-bind "1.2.1"]
                 [org.jibx/jibx-run "1.2.1"]
                 [log4j/log4j "1.2.16"]
                 [javax.mail/mail "1.4"]
                 [org.apache.axis2/mex "1.5.1"]
                 [org.apache.neethi/neethi "2.0.4"]
                 [jivesoftware/smack "3.0.4"]
                 [jivesoftware/smackx "3.0.4"]
                 [org.apache.axis2/soapmonitor "1.5.1"]
                 [org.apache.woden/woden-api "1.0M8"]
                 [org.apache.woden/woden-impl-dom "1.0M8"]
                 [wsdl4j/wsdl4j "1.6.2"]
                 [org.codehaus.woodstox/wstx-asl "3.2.4"]
                 [xalan/xalan "2.7.0"]
                 [xerces/xercesImpl "2.6.2"]
                 [xml-apis/xml-apis "1.3.02"]
                 [xml-resolver/xml-resolver "1.2"]
                 [org.apache.xmlbeans/xmlbeans "2.3.0"]
                 [com.betfair.api/APIDemo "1.0"]
                 ])

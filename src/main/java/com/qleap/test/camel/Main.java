package com.qleap.test.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.salesforce.SalesforceComponent;
import org.apache.camel.component.salesforce.SalesforceLoginConfig;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.salesforce.dto.Account;

public class Main {
	public static void main(String[] args) throws Exception {
		CamelContext camelContext = new DefaultCamelContext();
		// ConnectionFactory connectionFactory = new
		// ActiveMQConnectionFactory("vm://localhost");
		// camelContext.addComponent("jms",
		// JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
		SalesforceComponent sc = new SalesforceComponent(camelContext);
		sc.setPackages("org.apache.camel.salesforce.dto");

		SalesforceLoginConfig sflc = new SalesforceLoginConfig(
				"https://login.salesforce.com",
				"3MVG9Rd3qC6oMalUQa4Szl.shWl9KNC2Z9gtevBAGCBEM8zKv3HS5iQpUM7aJBl4qqgLMhDMu24Os1JFNVBQt",
				"730391782353589956", "h.agostinho.pires@q-leap.eu",
				"NA4fULZnDsMdN2mduN53seV3LvXt8W7zslJjB", true);
		sc.setLoginConfig(sflc);
		camelContext.addComponent("salesforce", sc);

		camelContext.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				// from("file:data/inbox?noop=true").to("file:data/outbox");
				// from("file:data/inbox?noop=true").to("vm:localhost");
				// from("file:c://tmp/data/inbox?noop=false").to("jms:queue:incomingOrders");
				// from("salesforce:AccountUpdates?notifyForFields=ALL&notifyForOperationUpdate=true&sObjectName=Account&updateTopic=true&sObjectQuery=SELECT Id, Name, Fax FROM Account").
				from("salesforce:AccountUpdate?sObjectName=Account").process(
						new Processor() {
							public void process(Exchange exchange) throws Exception {
								System.out.println(exchange.getIn().getBody(Account.class).toString());
								//System.out.println("We just downloaded: "+ exchange.getIn().getHeader("CamelFileName"));
							}
						}).to("log:org.apache.camel.example?level=DEBUG");
			}
		});

		// camelContext.addRoutes(new RouteBuilder() {
		// @Override
		// public void configure() throws Exception {
		// //from("file:data/outbox?noop=false").to("file:data/spam");
		// //from("vm:localhost").to("file:data/outbox?noop=false");
		// from("jms:queue:incomingOrders").
		// process(new Processor() {
		// public void process(Exchange exchange) throws Exception {
		// System.out.println("We just downloaded: "
		// + exchange.getIn().getHeader("CamelFileName"));
		// }
		// }).
		// to("file:c://tmp/data/outbox?noop=false");
		// }
		// });

		camelContext.start();

		Thread.sleep(1000000000);
		camelContext.stop();
	}
}

package com.digitalstack.logistics;

import com.digitalstack.logistics.company_manager.CompanyManager;
import com.digitalstack.logistics.model.dto.DestinationDto;
import com.digitalstack.logistics.model.dto.OrderDto;
import com.digitalstack.logistics.model.entity.Destination;
import com.digitalstack.logistics.model.entity.Order;
import com.digitalstack.logistics.repository.DestinationRepository;
import com.digitalstack.logistics.repository.OrdersRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Iterator;

import static com.digitalstack.logistics.helpers.OrderStatus.*;
import static com.digitalstack.logistics.model.converter.OrderConverter.formatter;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LogisticsApplicationTests {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	DestinationRepository destinationRepository;
	@Autowired
	OrdersRepository ordersRepository;
	@Autowired
	CompanyManager companyManager;


	@BeforeEach
	public void setUp() {
	}

	@AfterEach
	public void cleanUp() {
		ordersRepository.deleteAll();
		destinationRepository.deleteAll();
	}

	/**
	 * Steps:
	 * Create destination
	 * Create 2 orders for the destination
	 * Assert order status is NEW
	 * Start new day
	 * Assert order status is DELIVERING
	 * Cancel one of the orders
	 * Wait for delivery to complete
	 * Assert canceled order status is CANCELED
	 * Assert delivered order status is DELIVERED
	 * Assert company profit is distance * 1 delivered order
	 */
	@Test
	void testCancelOrderDuringDelivery() throws Exception
	{
		//Create destination
		DestinationDto destinationDto = getDestinationDto("Pitesti", 3);
		mockMvc.perform(post("/destinations/add")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(destinationDto)))
				.andExpect(MockMvcResultMatchers.status().isCreated());
		Long destinationId = destinationRepository.findAll().iterator().next().getId();

		//Create 2 orders for the destination
		String tomorrow = companyManager.getCurrentDate().plusDays(1).format(formatter);
		OrderDto [] payload = getOrderDtoAsArray(destinationId, tomorrow);
		mockMvc.perform(post("/orders/add")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(payload)))
				.andExpect(MockMvcResultMatchers.status().isOk());
		mockMvc.perform(post("/orders/add")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(payload)))
				.andExpect(MockMvcResultMatchers.status().isOk());

		//Assert order status is NEW
		Iterator<Order> iterator = ordersRepository.findAll().iterator();
		Order firstOrder = iterator.next();
		assertEquals(NEW, firstOrder.getStatus());
		Long firstOrderId = firstOrder.getId();
		Order secondOrder = iterator.next();
		assertEquals(NEW, secondOrder.getStatus());
		Long secondOrderId = secondOrder.getId();

		//Start new day
		mockMvc.perform(post("/shipping/new-day"))
				.andExpect(MockMvcResultMatchers.status().isOk());
		Thread.sleep(100);

		//Assert order status is DELIVERING
		iterator = ordersRepository.findAll().iterator();
		firstOrder = iterator.next();
		assertEquals(DELIVERING, firstOrder.getStatus());
		secondOrder = iterator.next();
		assertEquals(DELIVERING, secondOrder.getStatus());

		//Cancel one of the orders
		mockMvc.perform(post("/orders/cancel")
				.contentType("application/json")
				.content("[" + firstOrderId + "]"))
				.andExpect(MockMvcResultMatchers.status().isOk());

		//Wait for delivery to complete
		Thread.sleep(3200);

		//Assert canceled order status is CANCELED
		Order orderOne = ordersRepository.findById(firstOrderId)
				.orElseThrow();
		assertEquals(CANCELED, orderOne.getStatus());

		//Assert delivered order status is DELIVERED
		Order orderTwo = ordersRepository.findById(secondOrderId)
				.orElseThrow();
		assertEquals(DELIVERED, orderTwo.getStatus());

		//Assert company profit is distance * 1 delivered order
		MvcResult mvcResult = mockMvc.perform(get("/actuator/info")).andReturn();
		Integer profit = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.['Company profit']");
		assertEquals(3, profit);
	}

	@Test
	void testCreateValidDestination() throws Exception
	{
		//given
		DestinationDto destinationDto = getDestinationDto("Pitesti2", 5);

		//when
		mockMvc.perform(post("/destinations/add")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(destinationDto)))
				.andExpect(MockMvcResultMatchers.status().isCreated());

		//then
		Iterable<Destination> destinations = destinationRepository.findAll();
		Destination destination = destinations.iterator().next();
		assertNotNull(destination.getId());
		assertEquals(5, destination.getDistance());
		assertEquals("Pitesti2", destination.getName());
	}

	@Test
	void testCreateInvalidDestinationEmptyName() throws Exception
	{
		//given
		DestinationDto destinationDto = getDestinationDto("", 5);

		//when
		mockMvc.perform(post("/destinations/add")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(destinationDto)))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(content().string(containsString("Destination name is required!")));

		//then

	}

	@Test
	void testCreateInvalidDestinationEmptyDistance() throws Exception
	{
		//given

		//when
		mockMvc.perform(post("/destinations/add")
						.contentType("application/json")
						.content("{\"name\": \"Salaj\", \"distance\":  null}"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(content().string(containsString("Distance to destination must be set!")));

		//then

	}

	@Test
	void testCreateInvalidDestinationNegativeDistance() throws Exception
	{
		//given
		DestinationDto destinationDto = getDestinationDto("Pitesti", -5);

		//when
		mockMvc.perform(post("/destinations/add")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(destinationDto)))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(content().string(containsString("Distance must be positive integer!")));

		//then

	}

	private DestinationDto getDestinationDto(String name, Integer distance)
	{
		DestinationDto destinationDto = new DestinationDto();
		destinationDto.setName(name);
		destinationDto.setDistance(distance);
		return destinationDto;
	}

	private OrderDto [] getOrderDtoAsArray(Long destinationId, String deliveryDate) {
		OrderDto [] orderDtos = new OrderDto[1];
		OrderDto orderDto = new OrderDto();
		orderDto.setDestinationId(destinationId);
		orderDto.setDeliveryDate(deliveryDate);
		orderDtos[0] = orderDto;
		return orderDtos;
	}

}

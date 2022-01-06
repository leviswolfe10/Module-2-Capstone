package com.techelevator.view;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.techelevator.tenmo.model.TransferReturnedDTO;
import com.techelevator.tenmo.model.UserDTO;
import io.cucumber.java.an.E;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import com.techelevator.view.ConsoleService;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConsoleServiceTest {

	private ByteArrayOutputStream output;

	@Before
	public void setup() {
		output = new ByteArrayOutputStream();
	}

	@Test
	public void displays_a_list_of_menu_options_and_prompts_user_to_make_a_choice() {
		Object[] options = new Object[] { Integer.valueOf(3), "Blind", "Mice" };

		ConsoleService console = getServiceForTesting();

		console.getChoiceFromOptions(options);

		String expected = System.lineSeparator() + "1) " + options[0].toString() + System.lineSeparator() + "2) " + options[1].toString() + System.lineSeparator() + "3) "
				+ options[2].toString() + System.lineSeparator() + System.lineSeparator() + "Please choose an option >>> " + System.lineSeparator();
		Assert.assertEquals(expected, output.toString());
	}

	@Test
	public void returns_object_corresponding_to_user_choice() {
		Integer expected = Integer.valueOf(456);
		Integer[] options = new Integer[] { Integer.valueOf(123), expected, Integer.valueOf(789) };
		ConsoleService console = getServiceForTestingWithUserInput("2" + System.lineSeparator());

		Integer result = (Integer) console.getChoiceFromOptions(options);

		Assert.assertEquals(expected, result);
	}
	
	@Test
	public void prints_a_blank_line_after_successful_choice() {
		Integer[] options = new Integer[] { 123, 456, 789 };
		ConsoleService console = getServiceForTestingWithUserInput("2" + System.lineSeparator());

		Integer result = (Integer) console.getChoiceFromOptions(options);

		String expected = System.lineSeparator() + "1) " + options[0].toString() + System.lineSeparator() + "2) " + options[1].toString() + System.lineSeparator() + "3) "
				+ options[2].toString() + System.lineSeparator() + System.lineSeparator() + "Please choose an option >>> " + System.lineSeparator();
		
		Assert.assertEquals(expected, output.toString());
	}

	@Test
	public void redisplays_menu_if_user_does_not_choose_valid_option() {
		Object[] options = new Object[] { "Larry", "Curly", "Moe" };
		ConsoleService console = getServiceForTestingWithUserInput("4" + System.lineSeparator() + "1" + System.lineSeparator());

		console.getChoiceFromOptions(options);

		String menuDisplay = System.lineSeparator() + "1) " + options[0].toString() + System.lineSeparator() + "2) " + options[1].toString() + System.lineSeparator() + "3) "
				+ options[2].toString() + System.lineSeparator() + System.lineSeparator() + "Please choose an option >>> ";

		String expected = menuDisplay + System.lineSeparator() + "*** 4 is not a valid option ***" + System.lineSeparator() + System.lineSeparator() + menuDisplay + System.lineSeparator();

		Assert.assertEquals(expected, output.toString());
	}

	@Test
	public void redisplays_menu_if_user_chooses_option_less_than_1() {
		Object[] options = new Object[] { "Larry", "Curly", "Moe" };
		ConsoleService console = getServiceForTestingWithUserInput("0" + System.lineSeparator() + "1" + System.lineSeparator());

		console.getChoiceFromOptions(options);

		String menuDisplay = System.lineSeparator() + "1) " + options[0].toString() + System.lineSeparator() + "2) " + options[1].toString() + System.lineSeparator() + "3) "
				+ options[2].toString() + System.lineSeparator() + System.lineSeparator() + "Please choose an option >>> ";

		String expected = menuDisplay + System.lineSeparator() + "*** 0 is not a valid option ***" + System.lineSeparator() + System.lineSeparator() + menuDisplay + System.lineSeparator();

		Assert.assertEquals(expected, output.toString());
	}

	@Test
	public void redisplays_menu_if_user_enters_garbage() {
		Object[] options = new Object[] { "Larry", "Curly", "Moe" };
		ConsoleService console = getServiceForTestingWithUserInput("Mickey Mouse" + System.lineSeparator() + "1" + System.lineSeparator());

		console.getChoiceFromOptions(options);

		String menuDisplay = System.lineSeparator() + "1) " + options[0].toString() + System.lineSeparator() + "2) " + options[1].toString() + System.lineSeparator() + "3) "
				+ options[2].toString() + System.lineSeparator() + System.lineSeparator() + "Please choose an option >>> ";

		String expected = menuDisplay + System.lineSeparator() + "*** Mickey Mouse is not a valid option ***" + System.lineSeparator() + System.lineSeparator() + menuDisplay + System.lineSeparator();

		Assert.assertEquals(expected, output.toString());
	}
	
	@Test
	public void displays_prompt_for_user_input() {
		ConsoleService console = getServiceForTesting();
		String prompt = "Your Name";
		String expected = "Your Name: ";
		console.getUserInput(prompt);
		Assert.assertEquals(expected, output.toString());
	}
	
	@Test
	public void returns_user_input() {
		String expected = "Juan";
		ConsoleService console = getServiceForTestingWithUserInput(expected);
		String result = console.getUserInput("Your Name");
		Assert.assertEquals(expected, result);
	}
	
	@Test
	public void displays_prompt_for_user_input_integer() {
		ConsoleService console = getServiceForTesting();
		String prompt = "Your Age";
		String expected = "Your Age: ";
		console.getUserInputInteger(prompt);
		Assert.assertEquals(expected, output.toString());
	}
	
	@Test
	public void returns_user_input_for_integer() {
		Integer expected = 27;
		ConsoleService console = getServiceForTestingWithUserInput(expected.toString());
		Integer result = console.getUserInputInteger("Enter a number");
		Assert.assertEquals(expected, result);
	}
	
	@Test
	public void shows_error_and_redisplays_prompt_if_user_enters_invalid_integer() {
		ConsoleService console = getServiceForTestingWithUserInput("bogus" + System.lineSeparator() + "1" + System.lineSeparator());
		String prompt = "Your Age";
		String expected = "Your Age: " + System.lineSeparator() + "*** bogus is not valid ***" + System.lineSeparator() + System.lineSeparator() + "Your Age: ";
		console.getUserInputInteger(prompt);
		Assert.assertEquals(expected, output.toString());
	}

	private ConsoleService getServiceForTestingWithUserInput(String userInput) {
		ByteArrayInputStream input = new ByteArrayInputStream(String.valueOf(userInput).getBytes());
		return new ConsoleService(input, output);
	}

	private ConsoleService getServiceForTesting() {
		return getServiceForTestingWithUserInput("1" + System.lineSeparator());
	}

    @Test
    public void getUserInputDecimalpass_in_20_20_string_expect_20_20_double() {

		Double expected = 20.20;
		ConsoleService consoleService = getServiceForTestingWithUserInput(expected.toString());
		Double actual = consoleService.getUserInputDecimal("input test");
		Assert.assertEquals(expected, actual);
    }

	@Test
	public void getUserInputDecimalpass_in_19_string_expect_19_00_double() {
		Double expected = 19.00;
		String test = "19";
		ConsoleService consoleService = getServiceForTestingWithUserInput(test);
		Double actual = consoleService.getUserInputDecimal("input test");
		Assert.assertEquals(expected, actual);
	}


    @Test
    public void getSenderUsername_pass_in_sam_100_in_DTO_expect_sam_as_sender_account() {
		ConsoleService consoleService = getServiceForTesting();

		String expected = "Sam";
		TransferReturnedDTO transferDTO = new TransferReturnedDTO();
		transferDTO.setAccountFrom(100);
		UserDTO testUserDTO = new UserDTO();
		testUserDTO.setAccountId(100);
		testUserDTO.setUsername("Sam");
		List<UserDTO> testList = new ArrayList<>();
		testList.add(testUserDTO);
		boolean testBoolean = true;

		String actual = consoleService.getSenderUsername(transferDTO, testList, testBoolean);

		Assert.assertEquals(expected, actual);
    }

	@Test
	public void getSenderUsername_pass_in_olaf_299_in_DTO_expect_olaf_as_sender_account() {
		ConsoleService consoleService = getServiceForTesting();

		String expected = "olaf";
		TransferReturnedDTO transferDTO = new TransferReturnedDTO();
		transferDTO.setAccountFrom(299);
		UserDTO testUserDTO = new UserDTO();
		testUserDTO.setAccountId(299);
		testUserDTO.setUsername("olaf");
		List<UserDTO> testList = new ArrayList<>();
		testList.add(testUserDTO);
		boolean testBoolean = true;

		String actual = consoleService.getSenderUsername(transferDTO, testList, testBoolean);

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void getSenderUsername_pass_in_olaf_299_in_DTO_expect_olaf_as_receiver_account() {
		ConsoleService consoleService = getServiceForTesting();

		String expected = "olaf";
		TransferReturnedDTO transferDTO = new TransferReturnedDTO();
		transferDTO.setAccountTo(299);
		UserDTO testUserDTO = new UserDTO();
		testUserDTO.setAccountId(299);
		testUserDTO.setUsername("olaf");
		List<UserDTO> testList = new ArrayList<>();
		testList.add(testUserDTO);
		boolean testBoolean = false;

		String actual = consoleService.getSenderUsername(transferDTO, testList, testBoolean);

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void getSenderUsername_pass_in_peggy_28_in_DTO_expect_peggy_as_receiver_account() {
		ConsoleService consoleService = getServiceForTesting();

		String expected = "peggy";
		TransferReturnedDTO transferDTO = new TransferReturnedDTO();
		transferDTO.setAccountTo(28);
		UserDTO testUserDTO = new UserDTO();
		testUserDTO.setAccountId(28);
		testUserDTO.setUsername("peggy");
		List<UserDTO> testList = new ArrayList<>();
		testList.add(testUserDTO);
		boolean testBoolean = false;

		String actual = consoleService.getSenderUsername(transferDTO, testList, testBoolean);

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void getSenderUsername_pass_in_olaf_299_in_DTO_expect_null_when_receiver() {
		ConsoleService consoleService = getServiceForTesting();

		String expected = null;
		TransferReturnedDTO transferDTO = new TransferReturnedDTO();
		transferDTO.setAccountTo(299);
		UserDTO testUserDTO = new UserDTO();
		testUserDTO.setAccountId(299);
		testUserDTO.setUsername("olaf");
		List<UserDTO> testList = new ArrayList<>();
		boolean testBoolean = false;

		String actual = consoleService.getSenderUsername(transferDTO, testList, testBoolean);

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void getSenderUsername_pass_in_olaf_29_in_DTO_expect_null_when_sender() {
		ConsoleService consoleService = getServiceForTesting();

		String expected = null;
		TransferReturnedDTO transferDTO = new TransferReturnedDTO();
		transferDTO.setAccountTo(29);
		UserDTO testUserDTO = new UserDTO();
		testUserDTO.setAccountId(29);
		testUserDTO.setUsername("olaf");
		List<UserDTO> testList = new ArrayList<>();
		boolean testBoolean = true;

		String actual = consoleService.getSenderUsername(transferDTO, testList, testBoolean);

		Assert.assertEquals(expected, actual);
	}

    @Test
    public void getUsernameFromAccountId_pass_in_userdto_of_margaret_29_expect_margaret() {
		ConsoleService consoleService = getServiceForTesting();
		int testAccountNumber = 29;
		UserDTO testUserDTO = new UserDTO();
		testUserDTO.setAccountId(testAccountNumber);
		testUserDTO.setUsername("Margaret");
		List<UserDTO> testList = new ArrayList<>();
		testList.add(testUserDTO);

		String expected = "Margaret";

		String actual = consoleService.getUsernameFromAccountId(testList,testAccountNumber);

		Assert.assertEquals(expected, actual);
    }

	@Test
	public void getUsernameFromAccountId_pass_in_josh_19_expect_josh() {
		ConsoleService consoleService = getServiceForTesting();
		int testAccountNumber = 19;
		UserDTO testUserDTO = new UserDTO();
		testUserDTO.setAccountId(testAccountNumber);
		testUserDTO.setUsername("Josh");
		List<UserDTO> testList = new ArrayList<>();
		testList.add(testUserDTO);

		String expected = "Josh";

		String actual = consoleService.getUsernameFromAccountId(testList,testAccountNumber);

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void getUsernameFromAccountId_pass_in_josh_19_expect_empty_string() {
		ConsoleService consoleService = getServiceForTesting();
		int testAccountNumber = 19;
		UserDTO testUserDTO = new UserDTO();
		testUserDTO.setAccountId(testAccountNumber);
		testUserDTO.setUsername("Josh");
		List<UserDTO> testList = new ArrayList<>();


		String expected = "";

		String actual = consoleService.getUsernameFromAccountId(testList,testAccountNumber);

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void getUsernameFromAccountId_pass_in_bill_22_expect_empty_string() {
		ConsoleService consoleService = getServiceForTesting();
		int testAccountNumber = 22;
		UserDTO testUserDTO = new UserDTO();
		testUserDTO.setAccountId(testAccountNumber);
		testUserDTO.setUsername("Bill");
		List<UserDTO> testList = new ArrayList<>();


		String expected = "";

		String actual = consoleService.getUsernameFromAccountId(testList,testAccountNumber);

		Assert.assertEquals(expected, actual);
	}
}

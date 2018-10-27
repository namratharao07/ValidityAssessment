package com.my.spring;

import java.text.DateFormat;
import info.debatty.java.stringsimilarity.Levenshtein;
import info.debatty.java.stringsimilarity.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.my.spring.dao.CSVDao;
import com.my.spring.pojo.UserDetails;
import org.apache.commons.lang3.StringUtils;


@Controller
public class HomeController {

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView home(Locale locale, Model model) 
	{
	
		// Parsing the csv file to get content
		CSVDao csvdao = new CSVDao();
		ArrayList<UserDetails> list = csvdao.getContent("normal");
		ArrayList<String> listOfStrings = new ArrayList<String>();
		HashMap<Integer, ArrayList<String>> map = new HashMap();
		ArrayList<String> nonDuplicateRecords = new ArrayList<String>();
		ArrayList<String> duplicateRecords = new ArrayList<String>();
		
		for (UserDetails u : list) 
		{
			listOfStrings.add(u.toString());
		}

		for (int i = 0; i < listOfStrings.size(); i++) 
		{
			ArrayList<String> duplicates = new ArrayList<String>();
			int count = 0;
			
			
			for (int j = i + 1; j < listOfStrings.size(); j++) 
			{
				
				MetricLCS l = new MetricLCS();
		        double dist = l.distance(listOfStrings.get(i),listOfStrings.get(j));
				
				
				if (dist < 0.5) 
				{
					if (count == 0) 
					{
						duplicates.add(listOfStrings.get(i));

					}
					duplicates.add(listOfStrings.get(j));
					count++;

				}
			}
			map.put(i, duplicates);
		}
		 
		for (Entry<Integer, ArrayList<String>> e : map.entrySet()) 
		{

			if (e.getValue().size() > 1) 
			{
				
				duplicateRecords.addAll(e.getValue());
			}

		}

		nonDuplicateRecords.addAll(listOfStrings);

		for (Entry<Integer, ArrayList<String>> e : map.entrySet()) 
		{

			nonDuplicateRecords.removeAll(e.getValue());
		}
		//duplicates
		System.out.println("Duplicates");
		for (String s : duplicateRecords) 
		{
			System.out.println(s);
		}
		//non duplicates
		System.out.println("Non-Duplicates");
		for (String s : nonDuplicateRecords) 
		{
			System.out.println(s);
		}
		//create map send to view
		HashMap<String, Object> viewMap = new HashMap();
		viewMap.put("Duplicates", map);
		viewMap.put("NonDuplicates", nonDuplicateRecords);
		return new ModelAndView("home", "viewMap", viewMap);
	}

}

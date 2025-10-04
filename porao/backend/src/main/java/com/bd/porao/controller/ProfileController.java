package com.bd.porao.controller;

import com.bd.porao.model.Role;
import com.bd.porao.model.User;
import com.bd.porao.model.TutorProfile;
import com.bd.porao.model.StudentProfile;
import com.bd.porao.repository.UserRepository;
import com.bd.porao.repository.TutorProfileRepository;
import com.bd.porao.repository.StudentProfileRepository;
import com.bd.porao.security.JwtAuthenticationToken;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ProfileController
{

    private final UserRepository users;
    private final TutorProfileRepository tutors;
    private final StudentProfileRepository students;



    public ProfileController(UserRepository users, TutorProfileRepository tutors, StudentProfileRepository students)
    {
        this.users = users;
        this.tutors = tutors;
        this.students = students;
    }

    private User currentUser()
    {
        var auth = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Long id = Long.valueOf(auth.getToken().getSubject());
        return users.findById(id).orElseThrow();
    }

    @GetMapping("/tutor/profile")
    public Map<String, Object> getTutor()
    {
        User u = currentUser();
        if (u.getRole() != Role.TUTOR)
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        TutorProfile p = Optional.ofNullable(u.getTutorProfile()).orElseGet(() ->
        {
            TutorProfile np = new TutorProfile();
            np.setUser(u);
            return tutors.save(np);
        });

        Map<String, Object> result = new HashMap<>();
        result.put("headline", p.getHeadline());
        result.put("subject", p.getSubject());
        result.put("ratePerHour", p.getRatePerHour());
        result.put("bio", p.getBio());
        result.put("languages", p.getLanguages());
        result.put("lat", p.getLat());
        result.put("lng", p.getLng());
        return result;
    }

    @PutMapping("/tutor/profile")
    public void saveTutor(@RequestBody Map<String, Object> body)
    {
        User u = currentUser();
        if (u.getRole() != Role.TUTOR)
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        TutorProfile p = Optional.ofNullable(u.getTutorProfile()).orElseGet(() ->
        {
            TutorProfile np = new TutorProfile();
            np.setUser(u);
            return np;
        });

        if (body.containsKey("headline")) p.setHeadline((String) body.get("headline"));
        if (body.containsKey("subject")) p.setSubject((String) body.get("subject"));
        if (body.containsKey("bio")) p.setBio((String) body.get("bio"));
        if (body.containsKey("languages")) p.setLanguages((String) body.get("languages"));
        if (body.containsKey("ratePerHour")) {
            p.setRatePerHour(Double.valueOf(body.get("ratePerHour").toString()));
        }
        if (body.containsKey("lat")) p.setLat(Double.valueOf(body.get("lat").toString()));
        if (body.containsKey("lng")) p.setLng(Double.valueOf(body.get("lng").toString()));

        tutors.save(p);
    }

    @GetMapping("/student/profile")
    public Map<String, Object> getStudent()
    {
        User u = currentUser();
        if (u.getRole() != Role.STUDENT)
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        StudentProfile p = Optional.ofNullable(u.getStudentProfile()).orElseGet(() ->
        {
            StudentProfile np = new StudentProfile();
            np.setUser(u);
            return students.save(np);
        });

        Map<String, Object> result = new HashMap<>();
        result.put("gradeLevel", p.getGradeLevel());
        result.put("interests", p.getInterests());
        return result;
    }

    @PutMapping("/student/profile")
    public void saveStudent(@RequestBody Map<String, Object> body)
    {
        User u = currentUser();
        if (u.getRole() != Role.STUDENT)
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        StudentProfile p = Optional.ofNullable(u.getStudentProfile()).orElseGet(() ->
        {
            StudentProfile np = new StudentProfile();
            np.setUser(u);
            return np;
        });

        if (body.containsKey("gradeLevel")) p.setGradeLevel((String) body.get("gradeLevel"));
        if (body.containsKey("interests")) p.setInterests((String) body.get("interests"));

        students.save(p);
    }

}

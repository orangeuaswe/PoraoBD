#  About the Project: PORAO – AI-Powered Tutoring Platform

Education should be **personalized, accessible, and intelligent**. That belief inspired us to build **PORAO**, a next-generation tutoring platform that uses **AI to match students with the perfect tutors** — based on real learning goals, not just filters and keywords.

---

##  Inspiration

The idea for PORAO came from the challenges students face finding qualified tutors who truly fit their learning style, budget, and schedule.  
Existing platforms felt impersonal and inefficient.  
We wanted to change that by combining **semantic AI**, **real-time communication**, and **smart automation** — all in one platform.

---

##  How We Built It

### Backend
- **Java 21** with **Spring Boot 3.2**
- RESTful APIs, JWT authentication, and WebSocket-based real-time chat
- **PostgreSQL** for persistence and **Spring Security** for access control
- Integrated **OpenAI API** for semantic tutor recommendations
- **bKash payment gateway** integration for seamless transactions

### Frontend
- **React 18**, **Vite**, and **Tailwind CSS**
- **Google Maps API** for geolocation-based tutor search
- Real-time chat with **SockJS + Stomp**
- OAuth login with **Google** and **Apple**
- Separate dashboards for **students** and **tutors**

---

##  What We Learned

We discovered how powerful **AI embeddings** can be for search relevance and personalization.  
Implementing **real-time chat** over WebSockets taught us valuable lessons in scalability and synchronization.  
Integrating **secure payments** and **OAuth login** strengthened our understanding of safe and user-friendly system design.

---

##  Challenges We Faced

- Optimizing AI embeddings for speed and accuracy  
- Handling **real-time chat synchronization** across devices  
- Managing complex **role-based access**  
- Ensuring **secure payment flows**  
- Deploying with **Docker** and CI/CD efficiently  

---

##  A Little Math

We used **cosine similarity** to rank tutors based on vector embeddings:

\[
\text{similarity}(A, B) = \frac{A \cdot B}{\|A\| \|B\|}
\]

This helped intelligently match students’ learning requirements with tutor expertise.

---

##  Closing Thoughts

PORAO isn’t just a tutoring app — it’s a step toward **AI-personalized education**.  
We believe the future of learning is one where technology truly understands students’ needs and connects them with the right mentors — effortlessly.

---

*Built with love by the PoraoBD Team*

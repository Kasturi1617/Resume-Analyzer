# 🤖 AI-Powered Resume Analyzer

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18.2.0-blue.svg)](https://reactjs.org/)
[![Vite](https://img.shields.io/badge/Vite-5.0.8-purple.svg)](https://vitejs.dev/)

An intelligent full-stack application that analyzes resumes using AI-powered algorithms to provide comprehensive scoring, skill matching, and career recommendations.

## 🌟 Features

### 📊 **Advanced Resume Analysis**
- **AI-Powered Scoring**: Sophisticated scoring algorithm with 50% skills, 30% experience, 20% content quality weighting
- **Smart Skill Matching**: Contextual skill extraction and matching with job descriptions
- **Content Quality Analysis**: Evaluates structure, quantifiable achievements, and professional tone
- **Market Intelligence**: Skills ranked by current market demand and trends

### 🎯 **Intelligent Recommendations**
- **Job-Specific Suggestions**: Tailored recommendations based on job description analysis
- **Skill Gap Analysis**: Identifies missing skills and provides learning priorities  
- **Career Advancement**: Role-specific suggestions for senior positions and leadership
- **Resume Optimization**: Structural and content improvement recommendations

### 🚀 **User Experience**
- **Drag & Drop Upload**: Intuitive PDF resume upload interface
- **Real-time Analysis**: Instant processing and results display
- **Visual Score Breakdown**: Interactive progress bars and skill visualization
- **Responsive Design**: Works seamlessly on desktop and mobile devices

## 🏗️ Architecture

┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
│ React Client │◄──►│ Spring Boot API │◄──►│ FastAPI NLP │
│ (Frontend) │ │ (Backend) │ │ (PDF Parser) │
└─────────────────┘ └─────────────────┘ └─────────────────┘
│ │ │
│ │ │
┌────▼────┐ ┌──────▼──────┐ ┌───────▼───────┐
│ Vite │ │ Maven │ │ Python │
│ Build │ │ Build │ │ uvicorn │
└─────────┘ └─────────────┘ └───────────────┘

text

## 🛠️ Tech Stack

### Backend
- **Java 17** - Latest LTS version with modern language features
- **Spring Boot 3.1.0** - Enterprise-grade framework with auto-configuration
- **Maven** - Dependency management and build automation
- **Jackson** - JSON processing for API responses

### Frontend  
- **React 18.2.0** - Modern UI library with concurrent features
- **Vite 5.0.8** - Lightning-fast build tool and dev server
- **Axios** - Promise-based HTTP client for API communication
- **CSS3** - Custom responsive styling with flexbox and grid

### AI/NLP Service
- **FastAPI** - High-performance Python web framework
- **Python** - PDF parsing and text extraction
- **NLP Libraries** - Advanced text processing and analysis

## 📁 Project Structure

```
resume-analyzer/
├── backend/ # Spring Boot Application
│ ├── src/main/java/
│ │ └── com/example/resumeAnalyzer/demo/
│ │ ├── config/
│ │ │ └── CorsConfig.java # CORS configuration
│ │ ├── controller/
│ │ │ └── ResumeController.java # REST API endpoints
│ │ ├── service/
│ │ │ ├── AnalysisService.java # Core analysis logic
│ │ │ ├── ScoreService.java # Advanced scoring engine
│ │ │ ├── FileStorageService.java # File management
│ │ │ └── ParserClient.java # FastAPI integration
│ │ ├── dto/
│ │ │ ├── AnalysisResponseDto.java # API response models
│ │ │ └── ParseResultDto.java # Parsing data models
│ │ └── DemoApplication.java # Main application class
│ ├── src/main/resources/
│ │ └── application.properties # Configuration
│ └── pom.xml # Maven dependencies
├── frontend/ # React Application
│ ├── src/
│ │ ├── components/
│ │ │ ├── ResumeUpload.jsx # Main upload component
│ │ │ └── ResumeUpload.css # Component styling
│ │ ├── App.jsx # Root component
│ │ └── index.css # Global styles
│ ├── package.json # Node dependencies
│ └── vite.config.js # Vite configuration
└── README.md # Project documentation
```
text

## 🚀 Quick Start

### Prerequisites
- **Java 17+** - [Download here](https://www.oracle.com/java/technologies/downloads/)
- **Node.js 18+** - [Download here](https://nodejs.org/)
- **Python 3.8+** - [Download here](https://www.python.org/downloads/)
- **Maven 3.6+** - [Download here](https://maven.apache.org/download.cgi)

### 1. Clone the Repository
git clone https://github.com/Kasturi1617/Resume-Analyzer.git
cd Resume-Analyzer

text

### 2. Start FastAPI Service (PDF Parser)
Navigate to FastAPI directory
cd fastapi-service
pip install -r requirements.txt
uvicorn main:app --reload --port 8000

text

### 3. Start Spring Boot Backend
Navigate to backend directory
cd backend
./mvnw spring-boot:run

text
**Backend will be available at:** `http://localhost:8080`

### 4. Start React Frontend
Navigate to frontend directory
cd frontend
npm install
npm run dev

text
**Frontend will be available at:** `http://localhost:5173`

## 📖 API Documentation

### Upload Resume for Analysis
POST /api/resumes/upload
Content-Type: multipart/form-data

Parameters:

file: PDF resume file (required)

jobDescription: Job description text (optional)

text

**Response:**
{
"resumeId": 1,
"status": "DONE",
"score": 75,
"skillsMatched": ["Java", "Spring Boot", "React"],
"skillsMissing": ["Docker", "AWS"],
"recommendations": [
"🎯 Learn Docker to match job requirements",
"📊 Add quantifiable achievements"
],
"parserResult": {
"rawText": "Resume content...",
"skills": ["Java", "Spring Boot"],
"emails": ["user@example.com"],
"phones": ["+1234567890"]
}
}

text

### Retrieve Analysis Results
GET /api/resumes/{id}/analysis

text

## 🎯 Usage Examples

### 1. Basic Resume Analysis
Upload a PDF resume without job description to get:
- ✅ **General resume quality score**
- ✅ **Structural improvement suggestions** 
- ✅ **Content enhancement recommendations**
- ✅ **Career development advice**

### 2. Job-Specific Analysis
Upload resume + job description to get:
- ✅ **Skill matching percentage**
- ✅ **Missing skills identification**
- ✅ **Role-specific recommendations**
- ✅ **Market demand insights**

## 🧪 Testing

### Backend Tests
cd backend
./mvnw test

text

### Frontend Tests
cd frontend
npm test

text

### Manual Testing
1. Start all services (FastAPI, Spring Boot, React)
2. Navigate to `http://localhost:5173`
3. Upload a PDF resume
4. Add job description (optional)
5. View analysis results

## 🔧 Configuration

### Backend Configuration (`application.properties`)
Server settings
server.port=8080

File upload settings
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

FastAPI integration
app.parser-url=http://localhost:8000/parse
app.upload-dir=./uploads

text

### CORS Configuration
CORS is configured to allow requests from:
- `http://localhost:5173` (Vite dev server)
- `http://localhost:3000` (React dev server)

## 🚀 Deployment

### Production Build
Build backend
cd backend
./mvnw clean package

Build frontend
cd frontend
npm run build

text

### Docker Deployment (Optional)
Example Dockerfile for backend
FROM openjdk:17-jdk-slim
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]

text

## 🤝 Contributing

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

## 👨‍💻 Author

**Kasturi Sanyal**
- GitHub: [@Kasturi1617](https://github.com/Kasturi1617)
- LinkedIn: [Kasturi Sanyal](https://linkedin.com/in/kasturi-sanyal)
- Email: kasturisanyal3@gmail.com

## 🔮 Future Enhancements

- [ ] **Multi-format Support** - Word, TXT file analysis
- [ ] **ATS Compatibility** - Applicant Tracking System optimization
- [ ] **Industry-Specific Analysis** - Tailored scoring for different sectors
- [ ] **Real-time Collaboration** - Team review and feedback features
- [ ] **Advanced Analytics** - Historical tracking and improvement metrics
- [ ] **API Rate Limiting** - Enhanced security and performance
- [ ] **User Authentication** - Account management and history

---

⭐ **Star this repository if it helped you!**

*Built with ❤️ for the developer community*

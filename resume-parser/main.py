from fastapi import FastAPI, UploadFile, File
import PyPDF2
import re
from typing import List

app = FastAPI()

# Extract text from PDF
def extract_text_from_pdf(file) -> str:
    pdf_reader = PyPDF2.PdfReader(file)
    text = ""
    for page in pdf_reader.pages:
        text += page.extract_text() or ""
    return text

# Extract emails with regex
def extract_emails(text: str) -> List[str]:
    return re.findall(r"[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}", text)

# Extract phone numbers (simple pattern)
def extract_phones(text: str) -> List[str]:
    return re.findall(r"\+?\d[\d\s\-]{7,}\d", text)

# Dummy skill list for demo
KNOWN_SKILLS = ["Java", "Spring Boot", "Python", "SQL", "React", "Docker"]

def extract_skills(text: str) -> List[str]:
    found = []
    for skill in KNOWN_SKILLS:
        if skill.lower() in text.lower():
            found.append(skill)
    return found

@app.post("/parse")
async def parse_resume(file: UploadFile = File(...)):
    text = extract_text_from_pdf(file.file)
    skills = extract_skills(text)
    emails = extract_emails(text)
    phones = extract_phones(text)

    return {
        "rawText": text,
        "skills": skills,
        "emails": emails,
        "phones": phones
    }

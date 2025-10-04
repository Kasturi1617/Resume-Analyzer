import React, { useState } from 'react';
import axios from 'axios';

const ResumeUpload = () => {
    const [file, setFile] = useState(null);
    const [jobDescription, setJobDescription] = useState('');
    const [loading, setLoading] = useState(false);
    const [result, setResult] = useState(null);
    const [error, setError] = useState('');

    const handleFileChange = (e) => {
        const selectedFile = e.target.files[0];
        if (selectedFile && selectedFile.type === 'application/pdf') {
            setFile(selectedFile);
            setError('');
        } else {
            setError('Please select a PDF file');
            setFile(null);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!file) {
            setError('Please select a file');
            return;
        }

        setLoading(true);
        setError('');

        const formData = new FormData();
        formData.append('file', file);

        // Add job description if provided
        if (jobDescription.trim()) {
            formData.append('jobDescription', jobDescription);
        }

        try {
            const response = await axios.post(
                'http://localhost:8090/api/resumes/upload',
                formData,
                {
                    headers: {
                        'Content-Type': 'multipart/form-data',
                    },
                }
            );

            setResult(response.data);
            console.log('Success:', response.data);
        } catch (error) {
            console.error('Error:', error);
            setError(error.response?.data?.message || 'Upload failed. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="max-w-4xl mx-auto p-6">
            <h1 className="text-3xl font-bold mb-8 text-center text-blue-600">
                🤖 AI Resume Analyzer
            </h1>

            {/* Upload Form */}
            <div className="bg-white rounded-lg shadow-lg p-6 mb-8">
                <form onSubmit={handleSubmit} className="space-y-6">
                    {/* File Upload */}
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            📄 Upload Resume (PDF only)
                        </label>
                        <input
                            type="file"
                            accept=".pdf"
                            onChange={handleFileChange}
                            className="block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-md file:border-0 file:text-sm file:font-semibold file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100"
                            required
                        />
                        {file && (
                            <p className="mt-1 text-sm text-green-600">
                                ✅ Selected: {file.name}
                            </p>
                        )}
                    </div>

                    {/* Job Description */}
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            💼 Job Description (Optional)
                        </label>
                        <textarea
                            value={jobDescription}
                            onChange={(e) => setJobDescription(e.target.value)}
                            placeholder="Paste the job description here for better skill matching..."
                            rows={6}
                            className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                        />
                        <p className="mt-1 text-xs text-gray-500">
                            Adding a job description will provide more accurate skill matching and recommendations
                        </p>
                    </div>

                    {/* Error Display */}
                    {error && (
                        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-md">
                            ❌ {error}
                        </div>
                    )}

                    {/* Submit Button */}
                    <button
                        type="submit"
                        disabled={!file || loading}
                        className={`w-full py-3 px-4 rounded-md font-semibold text-white transition-colors ${!file || loading
                                ? 'bg-gray-400 cursor-not-allowed'
                                : 'bg-blue-600 hover:bg-blue-700 active:bg-blue-800'
                            }`}
                    >
                        {loading ? (
                            <span className="flex items-center justify-center">
                                <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                                </svg>
                                Analyzing Resume...
                            </span>
                        ) : (
                            '🚀 Analyze Resume'
                        )}
                    </button>
                </form>
            </div>

            {/* Results Display */}
            {result && (
                <div className="bg-white rounded-lg shadow-lg p-6">
                    <h2 className="text-2xl font-bold mb-6 text-gray-800">
                        📊 Analysis Results
                    </h2>

                    {/* Score Display */}
                    <div className="bg-gradient-to-r from-blue-50 to-indigo-50 rounded-lg p-6 mb-6">
                        <div className="flex items-center justify-between mb-4">
                            <h3 className="text-lg font-semibold text-gray-800">Overall Score</h3>
                            <span className="text-3xl font-bold text-blue-600">
                                {result.score}/100
                            </span>
                        </div>
                        <div className="w-full bg-gray-200 rounded-full h-4">
                            <div
                                className="bg-gradient-to-r from-blue-500 to-indigo-600 h-4 rounded-full transition-all duration-500"
                                style={{ width: `${result.score}%` }}
                            ></div>
                        </div>
                        <p className="text-sm text-gray-600 mt-2">
                            {result.score >= 70 ? '🎉 Excellent match!' :
                                result.score >= 60 ? '👍 Good match!' :
                                    result.score >= 40 ? '⚡ Room for improvement' :
                                        '🔥 Needs significant improvement'}
                        </p>
                    </div>

                    {/* Skills Analysis */}
                    <div className="grid md:grid-cols-2 gap-6 mb-6">
                        {/* Matched Skills */}
                        <div>
                            <h3 className="text-lg font-semibold text-green-600 mb-3">
                                ✅ Skills Matched ({result.skillsMatched.length})
                            </h3>
                            <div className="space-y-2">
                                {result.skillsMatched.length > 0 ? (
                                    result.skillsMatched.map((skill, index) => (
                                        <span
                                            key={index}
                                            style={{ marginRight: '0.5rem', marginBottom: '0.5rem' }}
                                            className="inline-block bg-green-100 text-green-800 px-3 py-1 rounded-full text-sm font-medium mr-2 mb-2"
                                        >
                                            {skill}
                                        </span>
                                    ))
                                ) : (
                                    <p className="text-gray-500 italic">No matching skills found</p>
                                )}
                            </div>
                        </div>

                        {/* Missing Skills */}
                        <div>
                            <h3 className="text-lg font-semibold text-red-600 mb-3">
                                ❌ Skills Missing ({result.skillsMissing.length})
                            </h3>
                            <div className="space-y-2">
                                {result.skillsMissing.length > 0 ? (
                                    result.skillsMissing.map((skill, index) => (
                                        <span
                                            key={index}
                                            style={{ marginRight: '0.5rem', marginBottom: '0.5rem' }}
                                            className="inline-block bg-red-100 text-red-800 px-3 py-1 rounded-full text-sm font-medium mr-2 mb-2"
                                        >
                                            {skill}
                                        </span>
                                    ))
                                ) : (
                                    <p className="text-green-600 font-medium">🎯 Perfect match! No missing skills</p>
                                )}
                            </div>
                        </div>
                    </div>

                    {/* Recommendations */}
                    <div>
                        <h3 className="text-lg font-semibold text-indigo-600 mb-4">
                            💡 Recommendations
                        </h3>
                        <div className="bg-gray-50 rounded-lg p-4">
                            {result.recommendations.length > 0 ? (
                                <ul className="space-y-3">
                                    {result.recommendations.map((rec, index) => (
                                        <li key={index} className="flex items-start">
                                            <span className="text-indigo-500 mr-3 mt-1">•</span>
                                            <span className="text-gray-700">{rec}</span>
                                        </li>
                                    ))}
                                </ul>
                            ) : (
                                <p className="text-gray-500 italic">No specific recommendations at this time</p>
                            )}
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default ResumeUpload;

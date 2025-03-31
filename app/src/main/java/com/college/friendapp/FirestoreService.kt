//package com.example.littlelemon
//
//import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.coroutines.tasks.await
//
//object FirestoreService {
//
//    private val db = FirebaseFirestore.getInstance()
//
//    // Mark attendance for a student on a specific date
//    suspend fun markAttendance(studentId: String, date: String, isPresent: Boolean) {
//        val attendanceRef = db.collection("attendance").document(studentId)
//        try {
//            attendanceRef.update("attendance.$date", isPresent).await()
//        } catch (e: Exception) {
//            // If document doesn't exist, create it
//            val data = mapOf(
//                "attendance" to mapOf(date to isPresent)
//            )
//            attendanceRef.set(data).await()
//        }
//    }
//
//    // Get full attendance data for a student
//    suspend fun getAttendanceForStudent(studentId: String): Map<String, Boolean> {
//        val doc = db.collection("attendance").document(studentId).get().await()
//        val attendanceMap = doc.get("attendance") as? Map<String, Boolean>
//        return attendanceMap ?: emptyMap()
//    }
//}

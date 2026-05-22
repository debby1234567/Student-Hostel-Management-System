/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
 
import model.Otp;
 
/**
 *
 * @author CTRL-SHIFT Ltd
 */
public interface IOtpDAO {
    void save(Otp otp);
    void update(Otp otp);
    Otp findLatestByEmail(String email);
    void deleteByEmail(String email);
    void markAsUsed(Long otpId);

   
}
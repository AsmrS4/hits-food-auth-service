package com.example.demo.services;

import com.example.demo.dtos.*;
import com.example.demo.entities.*;
import com.example.demo.mappers.BinMapper;
import com.example.demo.mappers.FoodMapper;
import com.example.demo.repositories.BinRepository;
import com.example.demo.repositories.FoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BinService {
    private final BinRepository binRepository;
    private final FoodRepository foodRepository;
    private final BinMapper binMapper;
    private final FoodMapper foodMapper;

    public Bin getClientBin(UUID clientId) {
        BinEntity entity = binRepository.findByClientId(clientId).orElseGet(() -> {
            BinEntity bin = new BinEntity();
            bin.setClientId(clientId);
            return binRepository.save(bin);
        });
        return binMapper.toDto(entity);
    }

    public Bin addFoodToBin(UUID clientId, UUID foodId) {
        BinEntity bin = binRepository.findByClientId(clientId).orElseGet(() -> {
            BinEntity b = new BinEntity();
            b.setClientId(clientId);
            return binRepository.save(b);
        });
        FoodEntity food = foodRepository.findById(foodId)
                .orElseThrow(()-> new UsernameNotFoundException("Food not found"));
        bin.getFoodList().add(food);
        return binMapper.toDto(binRepository.save(bin));
    }

    public Bin removeFoodFromBin(UUID clientId, UUID foodId) {
        BinEntity bin = binRepository.findByClientId(clientId)
                .orElseThrow(()-> new UsernameNotFoundException("Bin not found"));
        List<FoodEntity> foodList = bin.getFoodList();
        for (Iterator<FoodEntity> iterator = foodList.iterator(); iterator.hasNext(); ) {
            FoodEntity food = iterator.next();
            if (food.getId().equals(foodId)) {
                iterator.remove();
                break;
            }
        }
        return binMapper.toDto(binRepository.save(bin));
    }
}



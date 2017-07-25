function H = getH

n = 256;
k = n/2;

F = zeros(k/4);

for i = 1:k/4
    for j = 1:k/4
        if mod(i+1,k) == j
            F(i,j) = 1;
        end
    end
end

I = eye(k/4);
O = zeros(k/4);

H = [mod(F^31+I, 2)   F^15   F^25       F^0     O       F^20    F^12    I;
     F^28   mod(F^30+I, 2)   F^29       F^24    I       O       F       F^20;
     F^8    F^0    mod(F^28+I, 2)       F^1     F^29    I       O       F^21;
     F^18   F^30    F^0     mod(F^30+I, 2)      F^25    F^26    I       O];

%spy(H);

end


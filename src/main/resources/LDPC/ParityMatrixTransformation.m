function [ G ] = ParityMatrixTransformation( H )
% function[G]=generatormatrix(H); given a sparse paritycheckmatrix
% H compute a generatormatrix G
Hp=H;
[m, n]= size(Hp);
%disp([m,n]);
%supposem<n!

colperm=1:n;

for j=1:m
    %find row to put as new row j
    i = find(Hp(j:m, j),1);
    if isempty(i)
        % do some column swapping!
        k=min(max(find(Hp(j,:)),j));
        if isempty(k)
            disp(['problem in row' num2str(j,0)]);
            continue;
        end
        temp = Hp(:,j);
        Hp(:,j)=Hp(:,k);
        Hp(:,k)=temp;
        temp=colperm(k);
        colperm(k)=colperm(j);
        colperm(j)=temp;
    end
    %swaprows
    %adjustindices!
    i = i+j-1;
    if (i ~= j)
        temp = Hp(j,:);
        Hp(j,:)=Hp(i,:);
        Hp(i,:)=temp;
    end %if
    % clearoutrestofcolumn
    K=find(Hp(:,j));
    K=K(K~=j);
    if ~ isempty(K)
        t1 = full(Hp(j,:));
        for k=K'
            t2 = full(Hp(k,:));
            temp=xor(t1, t2);
            Hp(k,:) = sparse(temp);
        end
    end
end % f o r
% now Hp = [Id_m A]
A = Hp ( : ,m+1:n ) ;
Return = [A eye(m)];
%disp(full(Return));
%disp(full(Hp));
%computeG
%[f,g] = size(A);
%disp([f, g]);
%disp(A);
Ik = eye (n-m);
%[c,v] = size(Ik);
%disp(Ik);
%disp([c,v]);
[ b , invperm]=sort ( colperm ) ;
G = [A; Ik ] ;
G=G(invperm , : ) ;
% consistencycheck:mod(H?G,2) should give all?zero matrix

%return the parity check matrix
G = Return;
end

